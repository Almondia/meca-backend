package com.almondia.meca.category.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.controller.dto.CategoryWithStatisticsResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryWithStatisticsAndRecommendDto;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.repository.MemberRepository;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

@ExtendWith(MockitoExtension.class)
class CategoryInfoCombinerTest {

	@InjectMocks
	private CategoryInfoCombiner categoryInfoCombiner;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CardRepository cardRepository;

	@Mock
	private CardHistoryRepository cardHistoryRepository;

	@Mock
	private CategoryRecommendRepository categoryRecommendRepository;

	@Nested
	@DisplayName("findCategoryWithStatisticsResponse 테스트")
	class FindCategoryWithStatisticsResponseTest {

		@Test
		@DisplayName("contents가 빈 경우 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenContentsIsEmpty() {
			// given
			int pageSize = 1;
			CategorySearchOption categorySearchOption = CategorySearchOption.builder().build();
			boolean shared = false;
			when(
				categoryRepository.findCategoriesByMemberId(pageSize, null, categorySearchOption, shared,
					null))
				.thenReturn(Collections.emptyList());

			// when
			List<CategoryWithStatisticsResponseDto> responseDtos = categoryInfoCombiner.findCategoryWithStatisticsResponse(
				pageSize, null, categorySearchOption, shared, null);

			// then
			Mockito.verify(categoryRepository)
				.findCategoriesByMemberId(pageSize, null, categorySearchOption, shared, null);
			assertThat(responseDtos).isEmpty();
		}

		@Test
		@DisplayName("contents가 존재하는 경우 조합된 리스트를 반환한다")
		void shouldReturnCombinedListWhenContentsIsNotEmpty() {
			// given
			int pageSize = 1;
			CategorySearchOption categorySearchOption = CategorySearchOption.builder().build();
			boolean shared = false;
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryRepository.findCategoriesByMemberId(pageSize, null, categorySearchOption, shared,
				null))
				.thenReturn(List.of(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId)));
			when(cardRepository.countCardsByCategoryIdIsDeletedFalse(anyList()))
				.thenReturn(Map.of(categoryId, 1L));
			when(categoryRecommendRepository.findRecommendCountByCategoryIds(anyList()))
				.thenReturn(Map.of(categoryId, 1L));
			when(cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(anyList()))
				.thenReturn(Map.of(categoryId, Pair.of(1.0, 1L)));

			// when
			List<CategoryWithStatisticsResponseDto> responseDtos = categoryInfoCombiner.findCategoryWithStatisticsResponse(
				pageSize, null, categorySearchOption, shared, null);

			// then
			Mockito.verify(categoryRepository, times(1))
				.findCategoriesByMemberId(pageSize, null, categorySearchOption, shared, null);
			Mockito.verify(cardRepository, times(1)).countCardsByCategoryIdIsDeletedFalse(anyList());
			Mockito.verify(categoryRecommendRepository, times(1)).findRecommendCountByCategoryIds(anyList());
			Mockito.verify(cardHistoryRepository, times(1)).findCardHistoryScoresAvgAndCountsByCategoryIds(anyList());
			assertThat(responseDtos).isNotEmpty();
			assertThat(responseDtos.get(0)).isInstanceOf(CategoryWithStatisticsResponseDto.class);
		}
	}

	@Nested
	@DisplayName("findSharedCategoryResponse 테스트")
	class FindSharedCategoryResponseTest {

		@Test
		@DisplayName("contents가 빈 경우 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenContentsIsEmpty() {
			// given
			int pageSize = 1;
			CategorySearchOption categorySearchOption = CategorySearchOption.builder().build();
			boolean shared = true;
			when(
				categoryRepository.findCategories(anyInt(), any(), any(), anyBoolean()))
				.thenReturn(Collections.emptyList());

			// when
			List<SharedCategoryResponseDto> responseDtos = categoryInfoCombiner.findSharedCategoryResponse(
				pageSize, null, categorySearchOption);

			// then
			Mockito.verify(categoryRepository, times(1))
				.findCategories(anyInt(), any(), any(), anyBoolean());
			assertThat(responseDtos).isEmpty();
		}

		@Test
		@DisplayName("contents가 존재하는 경우 조합된 리스트를 반환한다")
		void shouldReturnCombinedListWhenContentsIsNotEmpty() {
			// given
			int pageSize = 1;
			CategorySearchOption categorySearchOption = CategorySearchOption.builder().build();
			boolean shared = true;
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryRepository.findCategories(anyInt(), any(), any(), anyBoolean()))
				.thenReturn(List.of(CategoryTestHelper.generateSharedCategory("title", memberId, categoryId)));
			when(cardRepository.countCardsByCategoryIdIsDeletedFalse(anyList()))
				.thenReturn(Map.of(categoryId, 1L));
			when(categoryRecommendRepository.findRecommendCountByCategoryIds(anyList()))
				.thenReturn(Map.of(categoryId, 1L));
			when(memberRepository.findMemberMapByIds(any()))
				.thenReturn(Map.of(memberId, MemberTestHelper.generateMember(memberId)));

			// when
			List<SharedCategoryResponseDto> responseDtos = categoryInfoCombiner.findSharedCategoryResponse(
				pageSize, null, categorySearchOption);

			// then
			Mockito.verify(categoryRepository, times(1))
				.findCategories(anyInt(), any(), any(), anyBoolean());
			Mockito.verify(cardRepository, times(1)).countCardsByCategoryIdIsDeletedFalse(anyList());
			Mockito.verify(categoryRecommendRepository, times(1)).findRecommendCountByCategoryIds(anyList());
			Mockito.verify(memberRepository, times(1)).findMemberMapByIds(any());
			assertThat(responseDtos).isNotEmpty();
			assertThat(responseDtos.get(0)).isInstanceOf(SharedCategoryResponseDto.class);
		}
	}

	@Nested
	@DisplayName("findSharedCategoryWithStatisticsResponse 테스트")
	class FindSharedCategoryWithStatisticsResponseTest {

		@Test
		@DisplayName("contents가 빈 경우 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenContentsIsEmpty() {
			// given
			int pageSize = 1;
			Id memberId = Id.generateNextId();
			CategorySearchOption categorySearchOption = CategorySearchOption.builder().build();
			when(
				categoryRepository.findSharedCategoriesByRecommend(anyInt(), any(), any(), any()))
				.thenReturn(Collections.emptyList());

			// when
			List<SharedCategoryWithStatisticsAndRecommendDto> responseDtos = categoryInfoCombiner.findSharedCategoryWithStatisticsResponse(
				pageSize, null, categorySearchOption, memberId);

			// then
			Mockito.verify(categoryRepository, times(1))
				.findSharedCategoriesByRecommend(anyInt(), any(), any(), any());
			assertThat(responseDtos).isEmpty();
		}

		@Test
		@DisplayName("contents가 존재하는 경우 조합된 리스트를 반환한다")
		void shouldReturnCombinedListTest() {
			// given
			int pageSize = 1;
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();
			CategorySearchOption categorySearchOption = CategorySearchOption.builder().build();
			when(categoryRepository.findSharedCategoriesByRecommend(anyInt(), any(), any(), any()))
				.thenReturn(List.of(CategoryTestHelper.generateSharedCategory("title", memberId, categoryId)));
			when(cardRepository.countCardsByCategoryIdIsDeletedFalse(anyList()))
				.thenReturn(Map.of(categoryId, 1L));
			when(categoryRecommendRepository.findRecommendCountByCategoryIds(anyList()))
				.thenReturn(Map.of(categoryId, 1L));
			when(memberRepository.findMemberMapByIds(any()))
				.thenReturn(Map.of(memberId, MemberTestHelper.generateMember(memberId)));
			when(cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(anyList()))
				.thenReturn(Map.of(categoryId, Pair.of(1.0, 1L)));

			// when
			List<SharedCategoryWithStatisticsAndRecommendDto> responseDtos = categoryInfoCombiner.findSharedCategoryWithStatisticsResponse(
				pageSize, null, categorySearchOption, memberId);

			// then
			Mockito.verify(categoryRepository, times(1))
				.findSharedCategoriesByRecommend(anyInt(), any(), any(), any());
			Mockito.verify(cardRepository, times(1)).countCardsByCategoryIdIsDeletedFalse(anyList());
			Mockito.verify(categoryRecommendRepository, times(1)).findRecommendCountByCategoryIds(anyList());
			Mockito.verify(memberRepository, times(1)).findMemberMapByIds(any());
			Mockito.verify(cardHistoryRepository, times(1)).findCardHistoryScoresAvgAndCountsByCategoryIds(anyList());
			assertThat(responseDtos).isNotEmpty();
			assertThat(responseDtos.get(0)).isInstanceOf(SharedCategoryWithStatisticsAndRecommendDto.class);
		}
	}
}