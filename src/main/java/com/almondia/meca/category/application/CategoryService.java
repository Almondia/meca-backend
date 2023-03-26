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
import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.infra.querydsl.CategorySearchCriteria;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.controller.dto.OffsetPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortField;
import com.almondia.meca.common.infra.querydsl.SortOption;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CardRepository cardRepository;
	private final CardHistoryRepository cardHistoryRepository;
	private final CategoryChecker categoryChecker;

	@Transactional
	public CategoryResponseDto saveCategory(SaveCategoryRequestDto saveCategoryRequestDto, Id memberId) {
		Category category = CategoryFactory.genCategory(saveCategoryRequestDto, memberId);
		Category result = categoryRepository.save(category);
		return CategoryMapper.entityToCategoryResponseDto(result);
	}

	@Transactional
	public CategoryResponseDto updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto, Id categoryId,
		Id memberId) {
		Category category = categoryChecker.checkAuthority(categoryId, memberId);
		if (updateCategoryRequestDto.getTitle() != null) {
			category.changeTitle(updateCategoryRequestDto.getTitle());
		}
		if (updateCategoryRequestDto.getIsShared() != null) {
			category.changeShare(updateCategoryRequestDto.getIsShared());
		}
		return CategoryMapper.entityToCategoryResponseDto(category);
	}

	@Transactional(readOnly = true)
	public OffsetPage<CategoryResponseDto> getOffsetPagingCategoryResponseDto(
		int offset,
		int pageSize,
		CategorySearchCriteria criteria,
		SortOption<? extends SortField> sortOption
	) {
		return categoryRepository.findCategories(offset, pageSize, criteria, sortOption);
	}

	@Transactional
	public void deleteCategory(Id categoryId, Id memberId) {
		Category category = categoryChecker.checkAuthority(categoryId, memberId);
		List<Card> cards = cardRepository.findByCategoryId(categoryId);
		List<Id> cardIds = cards.stream().map(Card::getCardId).collect(Collectors.toList());
		List<CardHistory> cardHistories = cardHistoryRepository.findByCardIdIn(cardIds);
		cardHistories.forEach(CardHistory::delete);
		cards.forEach(Card::delete);
		category.delete();
	}

	@Transactional(readOnly = true)
	public CursorPage<CategoryWithHistoryResponseDto> findCursorPagingCategoryWithHistoryResponse(
		int pageSize,
		Id memberId,
		Id lastCategoryId) {
		return categoryRepository.findCategoryWithStatisticsByMemberId(
			pageSize, memberId, lastCategoryId);
	}
}
