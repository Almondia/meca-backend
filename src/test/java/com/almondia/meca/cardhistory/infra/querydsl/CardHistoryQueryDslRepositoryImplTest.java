package com.almondia.meca.cardhistory.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
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
	@DisplayName("FindCardHistoriesByCategoryId 메서드 테스트")
	class FindCardHistoriesByCardIdAndCategoryId {

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
			CursorPage<CardHistoryWithCardAndMemberResponseDto> result = cardHistoryRepository.findCardHistoriesByCategoryId(
				categoryId,
				10, null);

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
			assertThatThrownBy(
				() -> cardHistoryRepository.findCardHistoriesByCategoryId(categoryId, 1001, null)).isInstanceOf(
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
			assertThatThrownBy(
				() -> cardHistoryRepository.findCardHistoriesByCategoryId(categoryId, -1, null)).isInstanceOf(
				InvalidDataAccessApiUsageException.class);
		}

		@Test
		@DisplayName("lastCardHistoryId가 null이 아니면 해당 인덱스부터 조회")
		void shouldFindCardHistoryFromLastCardHistoryIdTest() {
			// given
			final Id memberId = Id.generateNextId();
			final Id solvedMemberId = Id.generateNextId();
			final Id categoryId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Member solvedMember = MemberTestHelper.generateMember(solvedMemberId);
			Member solvedMember1 = MemberTestHelper.generateMember(Id.generateNextId());
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			Card card = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(card.getCardId(), solvedMemberId);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(card.getCardId(),
				solvedMember1.getMemberId());
			persistAll(member, solvedMember, solvedMember1, category, card, cardHistory1, cardHistory2);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> result = cardHistoryRepository.findCardHistoriesByCategoryId(
				categoryId,
				10, cardHistory2.getCardHistoryId());

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
				cardHistory.getSolvedUserId(), 10, null);

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
				() -> cardHistoryRepository.findCardHistoriesBySolvedMemberId(cardHistory.getSolvedUserId(), 1001,
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
				() -> cardHistoryRepository.findCardHistoriesBySolvedMemberId(cardHistory.getSolvedUserId(), -1,
					null)).isInstanceOf(InvalidDataAccessApiUsageException.class);
		}

		@Test
		@DisplayName("lastCardHistoryId가 null이 아니면 해당 인덱스부터 조회")
		void shouldFindCardHistoryFromLastCardHistoryIdTest() {
			// given
			final Id memberId = Id.generateNextId();
			final Id solvedMemberId = Id.generateNextId();
			final Id categoryId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Member solvedMember = MemberTestHelper.generateMember(solvedMemberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			Card card = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(card.getCardId(), solvedMemberId);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(card.getCardId(), solvedMemberId);
			persistAll(member, solvedMember, category, card, cardHistory1, cardHistory2);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> result = cardHistoryRepository.findCardHistoriesBySolvedMemberId(
				solvedMemberId, 10, cardHistory2.getCardHistoryId());

			// then
			assertThat(result.getContents()).hasSize(2);
			assertThat(result.getHasNext()).isNull();
		}
	}

	private void persistAll(Object... entities) {
		for (Object entity : entities) {
			em.persist(entity);
		}
	}
}