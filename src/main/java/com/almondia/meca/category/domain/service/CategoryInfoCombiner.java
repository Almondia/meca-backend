package com.almondia.meca.category.domain.service;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.controller.dto.CategoryStatisticsDto;
import com.almondia.meca.category.controller.dto.CategoryWithStatisticsResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryWithStatisticsAndRecommendDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.repository.MemberRepository;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryInfoCombiner {

	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;
	private final CardRepository cardRepository;
	private final CardHistoryRepository cardHistoryRepository;
	private final CategoryRecommendRepository categoryRecommendRepository;

	public List<CategoryWithStatisticsResponseDto> findCategoryWithStatisticsResponse(
		int pageSize,
		@Nullable Id lastCategoryId,
		CategorySearchOption categorySearchOption,
		@Nullable Boolean shared,
		Id memberId
	) {
		List<Category> contents = categoryRepository.findCategoriesByMemberId(pageSize, lastCategoryId,
			categorySearchOption, shared, memberId);
		if (contents.isEmpty()) {
			return Collections.emptyList();
		}
		List<Id> categoryIds = contents.stream().map(Category::getCategoryId).collect(toList());
		Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(categoryIds);
		Map<Id, Long> recommendCounts = categoryRecommendRepository.findRecommendCountByCategoryIds(categoryIds);
		Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
			categoryIds);

		return combineStatistics(contents, counts, recommendCounts, statistics);
	}

	public List<SharedCategoryResponseDto> findSharedCategoryResponse(
		int pageSize,
		@Nullable Id lastCategoryId,
		CategorySearchOption searchOption
	) {
		List<Category> contents = categoryRepository.findCategories(pageSize, lastCategoryId, searchOption,
			true);
		if (contents.isEmpty()) {
			return Collections.emptyList();
		}
		Map<Id, Member> memberMap = memberRepository.findMemberMapByIds(contents.stream()
			.map(Category::getMemberId)
			.collect(toList()));
		List<Id> categoryIds = contents.stream().map(Category::getCategoryId).collect(toList());
		Map<Id, Long> recommendCounts = categoryRecommendRepository.findRecommendCountByCategoryIds(categoryIds);
		Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(categoryIds);

		return combineSharedStatistics(contents, counts, recommendCounts, memberMap);
	}

	public List<SharedCategoryWithStatisticsAndRecommendDto> findSharedCategoryWithStatisticsResponse(
		int pageSize,
		@Nullable Id lastCategoryId,
		CategorySearchOption categorySearchOption,
		Id idWhoRecommend
	) {
		List<Category> contents = categoryRepository.findSharedCategoriesByRecommend(pageSize, lastCategoryId,
			categorySearchOption, idWhoRecommend);
		if (contents.isEmpty()) {
			return Collections.emptyList();
		}
		List<Id> categoryIds = contents.stream().map(Category::getCategoryId).collect(toList());
		Map<Id, Member> memberMap = memberRepository.findMemberMapByIds(contents.stream()
			.map(Category::getMemberId)
			.collect(toList()));
		Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(categoryIds);
		Map<Id, Long> recommendCounts = categoryRecommendRepository.findRecommendCountByCategoryIds(categoryIds);
		Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
			categoryIds);

		return combineSharedCategoryResponseDto(contents, counts, recommendCounts, statistics, memberMap);
	}

	private List<CategoryWithStatisticsResponseDto> combineStatistics(List<Category> contents, Map<Id, Long> counts,
		Map<Id, Long> recommendCounts, Map<Id, Pair<Double, Long>> statistics) {
		return contents.stream()
			.map(category -> new CategoryWithStatisticsResponseDto(
				category,
				new CategoryStatisticsDto(
					statistics.getOrDefault(category.getCategoryId(), Pair.of(0.0, 0L)).getFirst(),
					statistics.getOrDefault(category.getCategoryId(), Pair.of(0.0, 0L)).getSecond(),
					counts.getOrDefault(category.getCategoryId(), 0L)
				),
				recommendCounts.getOrDefault(category.getCategoryId(), 0L)
			)).collect(toList());
	}

	private List<SharedCategoryResponseDto> combineSharedStatistics(List<Category> contents, Map<Id, Long> counts,
		Map<Id, Long> recommendCounts, Map<Id, Member> memberMap) {
		return contents.stream()
			.filter(category -> counts.get(category.getCategoryId()) != 0L)
			.map(category -> new SharedCategoryResponseDto(category, memberMap.get(category.getMemberId()),
				recommendCounts.get(category.getCategoryId())))
			.collect(toList());
	}

	private List<SharedCategoryWithStatisticsAndRecommendDto> combineSharedCategoryResponseDto(List<Category> contents,
		Map<Id, Long> counts, Map<Id, Long> recommendCounts, Map<Id, Pair<Double, Long>> statistics,
		Map<Id, Member> memberMap) {
		return contents.stream()
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
	}
}
