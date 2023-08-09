package com.almondia.meca.category.application;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.application.helper.CategoryFactory;
import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.CategoryStatisticsDto;
import com.almondia.meca.category.controller.dto.CategoryWithStatisticsResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryWithStatisticsAndRecommendDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.repository.MemberRepository;
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
		// search
		List<Category> contents = categoryRepository.findCategoriesByMemberId(pageSize, lastCategoryId, searchOption,
			null, memberId);
		if (contents.isEmpty()) {
			return CursorPage.empty(SortOrder.DESC);
		}
		List<Id> categoryIds = contents.stream().map(Category::getCategoryId).collect(toList());
		Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(categoryIds);
		Map<Id, Long> recommendCounts = categoryRecommendRepository.findRecommendCountByCategoryIds(categoryIds);
		Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
			categoryIds);

		// combine
		List<CategoryWithStatisticsResponseDto> categoryWithStatisticsResponseDtos = contents.stream()
			.map(category -> new CategoryWithStatisticsResponseDto(
				category,
				statistics.get(category.getCategoryId()).getFirst(),
				statistics.get(category.getCategoryId()).getSecond(),
				counts.getOrDefault(category.getCategoryId(), 0L),
				recommendCounts.getOrDefault(category.getCategoryId(), 0L)
			)).collect(toList());
		return CursorPage.<CategoryWithStatisticsResponseDto>builder()
			.lastIdExtractStrategy(CategoryWithStatisticsResponseDto::getCategoryId)
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
		// search
		List<Category> contents = categoryRepository.findCategories(pageSize, lastCategoryId, searchOption,
			true);
		if (contents.isEmpty()) {
			return CursorPage.empty(SortOrder.DESC);
		}
		Map<Id, Member> memberMap = memberRepository.findMemberMapByIds(contents.stream()
			.map(Category::getMemberId)
			.collect(toList()));
		List<Id> categoryIds = contents.stream().map(Category::getCategoryId).collect(toList());
		Map<Id, Long> recommendCounts = categoryRecommendRepository.findRecommendCountByCategoryIds(categoryIds);
		Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(categoryIds);

		// combine
		List<SharedCategoryResponseDto> sharedCategoryResponseDtos = contents.stream()
			.filter(category -> counts.get(category.getCategoryId()) != 0L)
			.map(category -> new SharedCategoryResponseDto(category, memberMap.get(category.getMemberId()),
				recommendCounts.get(category.getCategoryId())))
			.collect(toList());
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
		// search
		List<Category> contents = categoryRepository.findSharedCategoriesByRecommend(pageSize, lastCategoryId,
			categorySearchOption, idWhoRecommend);
		List<Id> categoryIds = contents.stream().map(Category::getCategoryId).collect(toList());
		Map<Id, Member> memberMap = memberRepository.findMemberMapByIds(contents.stream()
			.map(Category::getMemberId)
			.collect(toList()));
		Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(categoryIds);
		Map<Id, Long> recommendCounts = categoryRecommendRepository.findRecommendCountByCategoryIds(categoryIds);
		Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
			categoryIds);

		// combine
		List<SharedCategoryWithStatisticsAndRecommendDto> sharedCategoryResponseDtos = contents.stream()
			.filter(category -> counts.get(category.getCategoryId()) != 0L)
			.map(category -> {
				double scoreAvg = statistics.get(category.getCategoryId()).getFirst();
				long solveCount = statistics.get(category.getCategoryId()).getSecond();
				long totalCount = counts.get(category.getCategoryId());
				CategoryStatisticsDto statisticsDto = new CategoryStatisticsDto(scoreAvg, solveCount, totalCount);
				long recommendCount = recommendCounts.get(category.getCategoryId());
				return new SharedCategoryWithStatisticsAndRecommendDto(category, memberMap.get(category.getMemberId()),
					statisticsDto, recommendCount);
			})
			.collect(toList());
		return CursorPage.<SharedCategoryWithStatisticsAndRecommendDto>builder()
			.lastIdExtractStrategy(sharedCategoryDto -> sharedCategoryDto.getCategory().getCategoryId())
			.contents(sharedCategoryResponseDtos)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
	}
}
