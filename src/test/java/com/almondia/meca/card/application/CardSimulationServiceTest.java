package com.almondia.meca.card.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.controller.dto.CardCountGroupByScoreDto;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfiguration.class, CardSimulationService.class})
class CardSimulationServiceTest {

	@Autowired
	EntityManager em;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	CardRepository cardRepository;

	@Autowired
	CardHistoryRepository cardHistoryRepository;

	@Autowired
	CardSimulationService cardSimulationService;

	/**
	 * 삭제된 카테고리를 조회한 경우
	 * 본인 카테고리가 아니거나 남의 카테고리지만 공유가 되지 않은 경우 접근 불가 예외를 발생한다
	 * 내 카테고리의 경우 접근 가능하다
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
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			category.delete();
			em.persist(category);

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateRandom(categoryId, memberId, 100))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("본인 카테고리가 아니거나 남의 카테고리지만 공유가 되지 않은 경우 접근 불가 예외를 발생한다")
		void shouldThrowAccessDeniedExceptionWhenNotMyCategoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id otherMemberId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			em.persist(category);

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateRandom(categoryId, otherMemberId, 100))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("내 카테고리의 경우 접근 가능하다")
		void shouldAccessMyCategoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			em.persist(category);

			// when
			List<CardDto> randoms = cardSimulationService.simulateRandom(categoryId, memberId, 100);
			assertThat(randoms).isNotNull();
		}

		@Test
		@DisplayName("카테고리에 속한 카드가 없는 경우 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenCategoryHasNoCardsTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			em.persist(category);

			// when
			List<CardDto> randoms = cardSimulationService.simulateRandom(categoryId, memberId, 100);

			// then
			assertThat(randoms).isEmpty();
		}

		@Test
		@DisplayName("limit으로 제한한 카드 갯수로 출력한다")
		void shouldReturnLimitedCardsTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Id cardId3 = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, cardId1);
			Card card2 = CardTestHelper.genOxCard(memberId, categoryId, cardId2);
			Card card3 = CardTestHelper.genOxCard(memberId, categoryId, cardId3);
			em.persist(category);
			em.persist(card1);
			em.persist(card2);
			em.persist(card3);

			final int limit = 2;

			// when
			List<CardDto> randoms = cardSimulationService.simulateRandom(categoryId, memberId, limit);

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
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			category.delete();
			em.persist(category);

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateScore(categoryId, memberId, 100))
				.isInstanceOf(IllegalArgumentException.class);

		}

		@Test
		@DisplayName("본인 카테고리가 아니거나 남의 카테고리지만 공유가 되지 않은 경우 접근 불가 예외를 발생한다")
		void shouldThrowAccessDeniedExceptionWhenNotMyCategoryOrNotSharedTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id otherMemberId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			em.persist(category);

			// expect
			assertThatThrownBy(
				() -> cardSimulationService.simulateScore(categoryId, otherMemberId, 100))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("카테고리에 속한 카드가 없는 경우 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenCategoryHasNoCardsTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			em.persist(category);

			// when
			List<CardDto> result = cardSimulationService.simulateScore(categoryId, memberId, 100);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("limit으로 제한한 카드 갯수로 출력한다")
		void shouldReturnLimitedCardsTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Id cardId3 = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId,
				categoryId);
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, cardId1);
			Card card2 = CardTestHelper.genOxCard(memberId, categoryId, cardId2);
			Card card3 = CardTestHelper.genOxCard(memberId, categoryId, cardId3);
			em.persist(category);
			em.persist(card1);
			em.persist(card2);
			em.persist(card3);
			int limit = 2;

			// when
			List<CardDto> result = cardSimulationService.simulateScore(categoryId,
				memberId, limit);

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
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			category.delete();
			em.persist(category);

			// expect
			assertThatThrownBy(() -> cardSimulationService.findCardCountByScore(categoryId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("카테고리 있다면 조회시 평균점수 기반으로 한 갯수를 반환한다")
		void shouldReturnCardCountByScoreTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(cardId, memberId);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(cardId, memberId);
			em.persist(category);
			em.persist(card);
			em.persist(cardHistory);
			em.persist(cardHistory1);

			// when
			List<CardCountGroupByScoreDto> result = cardSimulationService.findCardCountByScore(categoryId);

			// then
			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("카테고리에 속한 카드가 없는 경우 평균 점수 0의 카드 ID를 반환한다")
		void shouldReturnZeroScoreCardIdTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			em.persist(category);
			em.persist(card);

			// when
			List<CardCountGroupByScoreDto> result = cardSimulationService.findCardCountByScore(categoryId);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getScore()).isZero();
			assertThat(result.get(0).getCount()).isNotZero();
		}

	}
}