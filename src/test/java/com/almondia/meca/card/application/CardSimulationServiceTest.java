package com.almondia.meca.card.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.controller.dto.CardCountGroupByScoreDto;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;

class CardSimulationServiceTest {

	CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

	CardRepository cardRepository = Mockito.mock(CardRepository.class);
	CardHistoryRepository cardHistoryRepository = Mockito.mock(CardHistoryRepository.class);

	CardSimulationService cardSimulationService = new CardSimulationService(cardRepository, cardHistoryRepository,
		categoryRepository);

	/**
	 * 삭제된 카테고리를 조회한 경우
	 * 본인 카테고리가 아니거나 남의 카테고리지만 공유가 되지 않은 경우 접근 불가 예외를 발생한다
	 * 카테고리에 속한 카드가 없는 경우 빈 리스트를 반환한다
	 * limit으로 제한한 카드 갯수로 출력한다
	 */
	@Nested
	@DisplayName("simulateRandom 테스트")
	class SimulationRandomTest {

		@Test
		@DisplayName("삭제된 카테고리를 조회한 경우")
		void shouldThrowIllegalArgumentExceptionWhenCategoryIsDeletedTest() {
			// given
			Mockito.doReturn(Optional.empty())
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(
					List.of(CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId())))
				.when(cardRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateRandom(categoryId, memberId, 100)).isInstanceOf(
				IllegalArgumentException.class);
		}

		@Test
		@DisplayName("본인 카테고리가 아니거나 남의 카테고리지만 공유가 되지 않은 경우 접근 불가 예외를 발생한다")
		void shouldThrowAccessDeniedExceptionWhenNotMyCategoryOrNotSharedTest() {
			// given
			Mockito.doReturn(Optional.of(
					CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(), Id.generateNextId())))
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(
					List.of(CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId())))
				.when(cardRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateRandom(categoryId, memberId, 100)).isInstanceOf(
				AccessDeniedException.class);
		}

		@Test
		@DisplayName("카테고리에 속한 카드가 없는 경우 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenCategoryHasNoCardsTest() {
			// given
			Mockito.doReturn(Optional.of(
					CategoryTestHelper.generateSharedCategory("title", Id.generateNextId(), Id.generateNextId())))
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(List.of()).when(cardRepository).findByCategoryIdAndIsDeleted(any(), anyBoolean());

			// when
			List<CardDto> randoms = cardSimulationService.simulateRandom(Id.generateNextId(),
				Id.generateNextId(), 100);

			// then
			assertThat(randoms).isEmpty();
		}

		@Test
		@DisplayName("limit으로 제한한 카드 갯수로 출력한다")
		void shouldReturnLimitedCardsTest() {
			// given
			Mockito.doReturn(Optional.of(
					CategoryTestHelper.generateSharedCategory("title", Id.generateNextId(), Id.generateNextId())))
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(
					List.of(CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId()),
						CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId()),
						CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId())))
				.when(cardRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			final int limit = 2;

			// when
			List<CardDto> randoms = cardSimulationService.simulateRandom(Id.generateNextId(),
				Id.generateNextId(), limit);

			// then
			assertThat(randoms).hasSize(limit);
		}
	}

	/**
	 * 삭제된 카테고리를 조회한 경우
	 * 본인 카테고리가 아니거나 남의 카테고리지만 공유가 되지 않은 경우 접근 불가 예외를 발생한다
	 * 카테고리에 속한 카드가 없는 경우 빈 리스트를 반환한다
	 * limit으로 제한한 카드 갯수로 출력한다
	 */
	@Nested
	@DisplayName("simulateScore 테스트")
	class SimulateScoreTest {

		@Test
		@DisplayName("삭제된 카테고리를 조회한 경우")
		void shouldThrowIllegalArgumentExceptionWhenCategoryIsDeletedTest() {
			// given
			Mockito.doReturn(Optional.empty())
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(
					List.of(CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId())))
				.when(cardRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateScore(categoryId, memberId, 100)).isInstanceOf(
				IllegalArgumentException.class);
		}

		@Test
		@DisplayName("본인 카테고리가 아니거나 남의 카테고리지만 공유가 되지 않은 경우 접근 불가 예외를 발생한다")
		void shouldThrowAccessDeniedExceptionWhenNotMyCategoryOrNotSharedTest() {
			// given
			Mockito.doReturn(Optional.of(
					CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(), Id.generateNextId())))
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(
					List.of(CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId())))
				.when(cardRepository)
				.findCardByCategoryIdScoreAsc(any(), anyInt());
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateScore(categoryId, memberId, 100)).isInstanceOf(
				AccessDeniedException.class);
		}

		@Test
		@DisplayName("카테고리에 속한 카드가 없는 경우 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenCategoryHasNoCardsTest() {
			// given
			Mockito.doReturn(Optional.of(
					CategoryTestHelper.generateSharedCategory("title", Id.generateNextId(), Id.generateNextId())))
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(List.of()).when(cardRepository).findByCategoryIdAndIsDeleted(any(), anyBoolean());

			// when
			List<CardDto> result = cardSimulationService.simulateScore(Id.generateNextId(),
				Id.generateNextId(), 100);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("limit으로 제한한 카드 갯수로 출력한다")
		void shouldReturnLimitedCardsTest() {
			// given
			Mockito.doReturn(Optional.of(
					CategoryTestHelper.generateSharedCategory("title", Id.generateNextId(), Id.generateNextId())))
				.when(categoryRepository)
				.findByCategoryIdAndIsDeleted(any(), anyBoolean());
			Mockito.doReturn(
					List.of(CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId()),
						CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId())))
				.when(cardRepository).findCardByCategoryIdScoreAsc(any(), anyInt());
			final int limit = 2;

			// when
			List<CardDto> result = cardSimulationService.simulateScore(Id.generateNextId(),
				Id.generateNextId(), limit);

			// then
			assertThat(result).hasSize(limit);
		}
	}

	@Nested
	@DisplayName("findCardCountByScore 테스트")
	class FindCardCountByScoreTest {

		@Test
		@DisplayName("카테고리가 삭제되거나 없는 카테고리면 예외를 발생한다")
		void shouldThrowIllegalArgumentExceptionWhenCategoryIsDeletedTest() {
			// given
			Mockito.doReturn(false)
				.when(categoryRepository)
				.existsByCategoryIdAndIsDeletedFalse(any());
			Id randomId = Id.generateNextId();

			// expect
			Assert.assertThrows(IllegalArgumentException.class,
				() -> cardSimulationService.findCardCountByScore(randomId));
		}

		@Test
		@DisplayName("카테고리 있다면 조회시 평균점수 기반으로 한 갯수를 반환한다")
		void shouldReturnCardCountByScoreTest() {
			// given
			Mockito.doReturn(true)
				.when(categoryRepository)
				.existsByCategoryIdAndIsDeletedFalse(any());
			Map<Id, Double> map = new HashMap<>() {{
				put(Id.generateNextId(), 1.0);
				put(Id.generateNextId(), 2.0);
			}};
			Mockito.doReturn(map)
				.when(cardHistoryRepository)
				.findCardScoreAvgMapByCategoryId(any());
			Id categoryId = Id.generateNextId();

			// when
			List<CardCountGroupByScoreDto> result = cardSimulationService.findCardCountByScore(categoryId);

			// then
			assertThat(result.get(0)).extracting(CardCountGroupByScoreDto::getScore).isEqualTo(1.0);
		}
	}
}