package com.almondia.meca.category.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.application.helper.CategoryFactory;
import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.repository.MemberRepository;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CardRepository cardRepository;
	private final CardHistoryRepository cardHistoryRepository;
	private final CategoryChecker categoryChecker;
	private final MemberRepository memberRepository;
	private final CategoryRecommendRepository categoryRecommendRepository;

	@Transactional
	public CategoryDto saveCategory(SaveCategoryRequestDto saveCategoryRequestDto, Id memberId) {
		Category category = CategoryFactory.genCategory(saveCategoryRequestDto, memberId);
		Category result = categoryRepository.save(category);
		return CategoryMapper.entityToCategoryDto(result);
	}

	@Transactional
	public CategoryDto updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto, Id categoryId,
		Id memberId) {
		Category category = categoryChecker.checkAuthority(categoryId, memberId);
		if (updateCategoryRequestDto.getTitle() != null) {
			category.changeTitle(updateCategoryRequestDto.getTitle());
		}
		if (updateCategoryRequestDto.getThumbnail() != null) {
			category.changeThumbnail(updateCategoryRequestDto.getThumbnail());
		}
		if (updateCategoryRequestDto.getShared() != null) {
			category.changeShare(updateCategoryRequestDto.getShared());
		}
		return CategoryMapper.entityToCategoryDto(category);
	}

	@Transactional
	public void deleteCategory(Id categoryId, Id memberId) {
		Category category = categoryChecker.checkAuthority(categoryId, memberId);
		List<Card> cards = cardRepository.findByCategoryIdAndIsDeleted(categoryId, false);
		List<Id> cardIds = cards.stream().map(Card::getCardId).collect(Collectors.toList());
		List<CardHistory> cardHistories = cardHistoryRepository.findByCardIdInAndIsDeleted(cardIds, false);
		cardHistories.forEach(CardHistory::delete);
		cards.forEach(Card::delete);
		category.delete();
	}

	@Transactional(readOnly = true)
	public CursorPage<CategoryWithHistoryResponseDto> findCursorPagingCategoryWithHistoryResponse(
		int pageSize,
		Id memberId,
		Id lastCategoryId,
		CategorySearchOption categorySearchOption
	) {
		return categoryRepository.findCategoryWithStatisticsByMemberId(pageSize, memberId, lastCategoryId,
			categorySearchOption);
	}

	@Transactional(readOnly = true)
	public CursorPage<SharedCategoryResponseDto> findCursorPagingSharedCategoryResponseDto(
		int pageSize,
		Id lastCategoryId,
		CategorySearchOption searchOption
	) {
		// search
		List<Category> contents = categoryRepository.findSharedCategories(pageSize, lastCategoryId, searchOption);
		if (contents.isEmpty()) {
			return CursorPage.empty();
		}
		Member member = memberRepository.findById(contents.get(0).getMemberId())
			.orElseThrow(() -> new IllegalStateException("카테고리가 비정상적입니다. 멤버가 존재하지 않습니다"));
		List<Id> categoryIds = contents.stream().map(Category::getCategoryId).collect(Collectors.toList());
		Map<Id, Long> recommendCounts = categoryRecommendRepository.findRecommendCountByCategoryIds(categoryIds);
		Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(categoryIds);
		// combine
		List<SharedCategoryResponseDto> sharedCategoryResponseDtos = contents.stream()
			.filter(category -> counts.get(category.getCategoryId()) != 0L)
			.map(category -> new SharedCategoryResponseDto(category, member,
				recommendCounts.get(category.getCategoryId())))
			.collect(Collectors.toList());
		return CursorPage.of(sharedCategoryResponseDtos, pageSize, SortOrder.DESC);
	}
}
