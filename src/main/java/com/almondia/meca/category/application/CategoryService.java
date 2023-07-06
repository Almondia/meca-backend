package com.almondia.meca.category.application;

import java.util.List;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CardRepository cardRepository;
	private final CardHistoryRepository cardHistoryRepository;
	private final CategoryChecker categoryChecker;

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
		CategorySearchOption categorySearchOption
	) {
		return categoryRepository.findCategoryShared(pageSize, lastCategoryId, categorySearchOption);
	}
}
