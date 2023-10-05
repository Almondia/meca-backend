package com.almondia.meca.category.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.application.helper.CategoryFactory;
import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.CategoryWithStatisticsResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryWithStatisticsAndRecommendDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.domain.service.CategoryInfoCombiner;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryChecker categoryChecker;
	private final CategoryRepository categoryRepository;
	private final CardRepository cardRepository;
	private final CardHistoryRepository cardHistoryRepository;
	private final CategoryInfoCombiner categoryInfoCombiner;

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
		List<Id> cardIds = cards.stream().map(Card::getCardId).collect(toList());
		List<CardHistory> cardHistories = cardHistoryRepository.findByCardIdInAndIsDeleted(cardIds, false);
		cardHistories.forEach(CardHistory::delete);
		cards.forEach(Card::delete);
		category.delete();
	}

	@Transactional(readOnly = true)
	public CursorPage<CategoryWithStatisticsResponseDto> findCursorPagingCategoryWithHistoryResponse(
		int pageSize,
		Id memberId,
		Id lastCategoryId,
		CategorySearchOption searchOption
	) {
		List<CategoryWithStatisticsResponseDto> categoryWithStatisticsResponseDtos = categoryInfoCombiner.findCategoryWithStatisticsResponse(
			pageSize, lastCategoryId, searchOption, null, memberId);
		if (categoryWithStatisticsResponseDtos.isEmpty()) {
			return CursorPage.empty(SortOrder.DESC);
		}
		return CursorPage.<CategoryWithStatisticsResponseDto>builder()
			.lastIdExtractStrategy(categoryWithStatistics -> categoryWithStatistics.getCategory().getCategoryId())
			.contents(categoryWithStatisticsResponseDtos)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
	}

	@Transactional(readOnly = true)
	public CursorPage<SharedCategoryResponseDto> findCursorPagingSharedCategoryResponseDto(
		int pageSize,
		Id lastCategoryId,
		CategorySearchOption searchOption
	) {
		List<SharedCategoryResponseDto> sharedCategoryResponseDtos = categoryInfoCombiner.findSharedCategoryResponse(
			pageSize, lastCategoryId, searchOption);
		if (sharedCategoryResponseDtos.isEmpty()) {
			return CursorPage.empty(SortOrder.DESC);
		}
		return CursorPage.<SharedCategoryResponseDto>builder()
			.lastIdExtractStrategy(sharedCategoryDto -> sharedCategoryDto.getCategory().getCategoryId())
			.contents(sharedCategoryResponseDtos)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
	}

	@Transactional
	public CursorPage<SharedCategoryWithStatisticsAndRecommendDto> findSharedCategoryWithStatistics(
		int pageSize,
		Id lastCategoryId,
		CategorySearchOption categorySearchOption,
		Id idWhoRecommend
	) {
		List<SharedCategoryWithStatisticsAndRecommendDto> sharedCategoryWithStatisticsResponseDtos = categoryInfoCombiner.findSharedCategoryWithStatisticsResponse(
			pageSize, lastCategoryId, categorySearchOption, idWhoRecommend);
		if (sharedCategoryWithStatisticsResponseDtos.isEmpty()) {
			return CursorPage.empty(SortOrder.DESC);
		}
		return CursorPage.<SharedCategoryWithStatisticsAndRecommendDto>builder()
			.lastIdExtractStrategy(sharedCategoryDto -> sharedCategoryDto.getCategory().getCategoryId())
			.contents(sharedCategoryWithStatisticsResponseDtos)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
	}
}
