package com.almondia.meca.cardhistory.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.util.Pair;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.controller.dto.CardStatisticsDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.entity.Member;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfiguration.class})
class CardHistoryQueryDslRepositoryImplTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private CardHistoryRepository cardHistoryRepository;

	/**
	 * 삭제된 카드 히스토리는 조회되면 안된다
	 * 요청 pageSize는 1000이하만 가능하다
	 * 요청 pageSize는 0이상이어야 한다
	 * lastCardHistoryId가 null이 아니면 해당 인덱스부터 조회한다
	 */
	@Nested
	@DisplayName("FindCardHistoriesByCardId 메서드 테스트")
	class FindCardHistoriesByCardId {

		@Test
		@DisplayName("삭제된 카드 히스토리는 조회되면 안된다")
		void shouldNotFindDeletedCardHistoryTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			cardHistory.delete();
			em.persist(cardHistory);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> result = cardHistoryRepository.findCardHistoriesByCardId(
				cardId, 10,
				null);

			// then
			assertThat(result.getContents()).isEmpty();
			assertThat(result.getHasNext()).isNull();
		}

		@Test
		@DisplayName("요청 pageSize는 1000이하만 가능하다")
		void shouldNotFindCardHistoryWhenPageSizeIsOver1000Test() {
			// given
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			em.persist(cardHistory);

			// expect
			assertThatThrownBy(() -> cardHistoryRepository.findCardHistoriesByCardId(cardId, 1001, null)).isInstanceOf(
				InvalidDataAccessApiUsageException.class);
		}

		@Test
		@DisplayName("요청 pageSize는 0이상이어야 한다")
		void shouldNotFindCardHistoryWhenPageSizeIsUnder0Test() {
			// given
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			em.persist(cardHistory);

			// expect
			assertThatThrownBy(() -> cardHistoryRepository.findCardHistoriesByCardId(cardId, -1, null)).isInstanceOf(
				InvalidDataAccessApiUsageException.class);
		}

		@Test
		@DisplayName("lastCardHistoryId가 null이 아니면 해당 인덱스부터 조회한다")
		void shouldFindCardHistoryFromLastCardHistoryIdTest() {
			// given
			final Id categoryId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(Id.generateNextId());
			Member solvedMember = MemberTestHelper.generateMember(Id.generateNextId());
			Member solvedMember2 = MemberTestHelper.generateMember(Id.generateNextId());
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", member.getMemberId(), categoryId);
			Card card = CardTestHelper.genOxCard(member.getMemberId(), categoryId, Id.generateNextId());
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(card.getCardId(),
				solvedMember2.getMemberId());
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(card.getCardId(),
				solvedMember.getMemberId());
			persistAll(member, solvedMember, solvedMember2, category, card, cardHistory1, cardHistory2);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> result = cardHistoryRepository.findCardHistoriesByCardId(
				card.getCardId(), 100, cardHistory2.getCardHistoryId());

			// then
			assertThat(result.getContents()).hasSize(2);
			assertThat(result.getHasNext()).isNull();
		}
	}

	/**
	 * 삭제된 카드 히스토리는 조회되면 안된다
	 * 요청 pageSize는 1000이하만 가능하다
	 * 요청 pageSize는 0이상이어야 한다
	 * lastCardHistoryId가 null이 아니면 해당 인덱스부터 조회
	 */
	@Nested
	@DisplayName("FindCardHistoriesBySolvedMemberId 메서드 테스트")
	class FindCardHistoriesBySolvedMemberIdTest {

		@Test
		@DisplayName("삭제된 카드 히스토리는 조회되면 안된다")
		void shouldNotFindDeletedCardHistoryTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", Id.generateNextId(), categoryId);
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			cardHistory.delete();
			persistAll(category, cardHistory);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> result = cardHistoryRepository.findCardHistoriesBySolvedMemberId(
				cardHistory.getSolvedMemberId(), 10, null);

			// then
			assertThat(result.getContents()).isEmpty();
			assertThat(result.getHasNext()).isNull();
		}

		@Test
		@DisplayName("요청 pageSize는 1000이하만 가능하다")
		void shouldNotFindCardHistoryWhenPageSizeIsOver1000Test() {
			// given
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", Id.generateNextId(), categoryId);
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			persistAll(category, cardHistory);

			// expect
			assertThatThrownBy(
				() -> cardHistoryRepository.findCardHistoriesBySolvedMemberId(cardHistory.getSolvedMemberId(), 1001,
					null)).isInstanceOf(InvalidDataAccessApiUsageException.class);
		}

		@Test
		@DisplayName("요청 pageSize는 0이상이어야 한다")
		void shouldNotFindCardHistoryWhenPageSizeIsUnder0Test() {
			// given
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", Id.generateNextId(), categoryId);
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			persistAll(category, cardHistory);

			// expect
			assertThatThrownBy(
				() -> cardHistoryRepository.findCardHistoriesBySolvedMemberId(cardHistory.getSolvedMemberId(), -1,
					null)).isInstanceOf(InvalidDataAccessApiUsageException.class);
		}

		@Test
		@DisplayName("lastCardHistoryId가 null이 아니면 해당 인덱스부터 조회")
		void shouldFindCardHistoryFromLastCardHistoryIdTest() {
			// given
			final Id memberId = Id.generateNextId();
			final Id solvedMemberId = Id.generateNextId();
			final Id cardId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Member solvedMember = MemberTestHelper.generateMember(solvedMemberId);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(cardId, solvedMemberId);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(cardId, solvedMemberId);
			persistAll(member, solvedMember, cardHistory1, cardHistory2);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> result = cardHistoryRepository.findCardHistoriesBySolvedMemberId(
				solvedMemberId, 10, cardHistory2.getCardHistoryId());

			// then
			assertThat(result.getContents()).hasSize(2);
			assertThat(result.getHasNext()).isNull();
		}
	}

	@Nested
	@DisplayName("findCardHistoryScoresAvgAndCountsByCategoryIds 테스트")
	class FindCardHistoryScoresAvgAndCountsByCategoryIdsTest {

		@Test
		@DisplayName("카테고리 아이디 리스트에 해당하는 카드 히스토리의 평균 점수와 카드 히스토리를 가진 카드의 갯수를 조회한다")
		void shouldReturnAvgScoresAndCardHistoriesTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId1 = Id.generateNextId();
			Id categoryId2 = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Id cardHistoryId1 = Id.generateNextId();
			Id cardHistoryId2 = Id.generateNextId();
			Category category1 = CategoryTestHelper.generateUnSharedCategory("hello", Id.generateNextId(), categoryId1);
			Category category2 = CategoryTestHelper.generateSharedCategory("hello", Id.generateNextId(), categoryId2);
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId1, cardId1);
			Card card2 = CardTestHelper.genOxCard(memberId, categoryId2, cardId2);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(cardHistoryId1, cardId1, 10);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(cardHistoryId2, cardId1, 20);
			persistAll(category1, category2, card1, card2, cardHistory1, cardHistory2);

			// when
			Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
				List.of(categoryId1, categoryId2));

			// then
			assertThat(statistics).hasSize(2);
			assertThat(statistics.get(categoryId1).getFirst()).isEqualTo(15);
			assertThat(statistics.get(categoryId1).getSecond()).isEqualTo(1);

		}

		@Test
		@DisplayName("카테고리 아이디 리스트가 비어있으면 빈 리스트를 반환한다")
		void shouldReturnEmptyListWhenCategoryIdsIsEmptyTest() {
			// given
			List<Id> categoryIds = List.of();

			// when
			Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
				categoryIds);

			// then
			assertThat(statistics).isEmpty();
		}

		@Test
		@DisplayName("카테고리의 카드 히스토리 정보가 존재하지 않는 경우 해당 카테고리의 평균 점수는 0이다")
		void shouldReturnZeroAvgScoreWhenCardHistoriesIsEmptyTest() {
			// given
			Id categoryId = Id.generateNextId();
			List<Id> categoryIds = List.of(categoryId);

			// when
			Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
				categoryIds);

			// then
			assertThat(statistics.get(categoryId)).isEqualTo(Pair.of(0.0, 0L));
		}

		@Test
		@DisplayName("카테고리의 카드 히스토리 정보가 존재하지 않는 경우 해당 카테고리의 카드 히스토리 개수는 0이다")
		void shouldReturnZeroCardHistoryCountWhenCardHistoriesIsEmptyTest() {
			// given
			Id categoryId = Id.generateNextId();
			List<Id> categoryIds = List.of(categoryId);

			// when
			Map<Id, Pair<Double, Long>> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCategoryIds(
				categoryIds);

			// then
			assertThat(statistics.get(categoryId)).isEqualTo(Pair.of(0.0, 0L));
		}
	}

	@Nested
	@DisplayName("findCardHistoryScoresAvgAndCountsByCardId 테스트")
	class FindCardHistoryScoresAvgAndCountsByCardIdTest {

		@Test
		@DisplayName("카드 히스토리가 존재하지 않는다면 통계값이 모두 0")
		void shouldReturnZeroWhenNotExistCardHistoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			em.persist(card);

			// when
			Optional<CardStatisticsDto> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCardId(
				cardId);

			// then
			assertThat(statistics).isEmpty();

		}

		@Test
		@DisplayName("카드 히스토리가 모두 삭제된 상태라면 Optional.empty()")
		void shouldReturnEmptyWhenCardHistoryDeletedTest() {
			//given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			cardHistory.delete();
			em.persist(card);

			// when
			Optional<CardStatisticsDto> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCardId(
				cardId);

			// then
			assertThat(statistics).isEmpty();
		}

		@Test
		@DisplayName("다른 카드 ID의 히스토리 갯수는 영향을 주지 않는다")
		void shouldNotEffectAnotherCardIdHistoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, cardId1);
			Card card2 = CardTestHelper.genOxCard(memberId, categoryId, cardId2);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId1, 10);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId2, 20);
			persistAll(card1, card2, cardHistory1, cardHistory2);

			// when
			CardStatisticsDto statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCardId(cardId1)
				.orElseThrow();

			// then
			assertThat(statistics.getScoreAvg()).isEqualTo(10.0);
			assertThat(statistics.getSolveCount()).isEqualTo(1L);
		}

		@Test
		@DisplayName("카드 히스토리의 갯수만큼 출력되며 평균 점수는 카드 히스토리 갯수에 기반한다")
		void shouldReturnAvgAndHistoryCountTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 20);
			persistAll(card, cardHistory1, cardHistory2);

			// when
			CardStatisticsDto statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCardId(cardId)
				.orElseThrow();

			// then
			assertThat(statistics.getScoreAvg()).isEqualTo(15.0);
			assertThat(statistics.getSolveCount()).isEqualTo(2L);
		}

		@Test
		@DisplayName("삭제된 카드 Id에 대해서는 조회하지 않는다")
		void shouldNotReturnCardWhenDeletedCardTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			card.delete();
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 10);
			persistAll(card, cardHistory1);

			// when
			Optional<CardStatisticsDto> statistics = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCardId(
				cardId);

			// then
			assertThat(statistics).isEmpty();
		}
	}

	private void persistAll(Object... entities) {
		for (Object entity : entities) {
			em.persist(entity);
		}
	}
}