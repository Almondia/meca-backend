package com.almondia.meca.card.infra.querydsl;

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

import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.entity.Member;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class CardQueryDslRepositoryImplTest {

	@Autowired
	CardRepository cardRepository;

	@Autowired
	EntityManager entityManager;

	/**
	 * 카테고리에 속한 카드를 조회한다
	 * 삭제된 카드는 조회하지 않는다
	 * 가득찬 경우 항상 pageSize보다 하나 더 조회한다
	 * lastCardId를 입력한 경우 lastCardId보다 같거나 작은 cardId만 조회된다
	 */
	@Nested
	@DisplayName("findCardByCategoryId테스트")
	class FindCardByCategoryIdTest {

		@Test
		@DisplayName("카테고리에 속한 카드를 조회한다")
		void shouldReturnCardWhenInCategoryTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id categoryId2 = Id.generateNextId();
			Id memberId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			entityManager.persist(MemberTestHelper.generateMember(memberId));
			entityManager.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));
			entityManager.persist(CategoryTestHelper.generateUnSharedCategory("title2", memberId, categoryId2));
			entityManager.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId));

			// when
			List<CardDto> cards = cardRepository.findCardByCategoryId(10, null, categoryId2,
				CardSearchOption.builder().build());

			// then
			assertThat(cards).isEmpty();
		}

		@Test
		@DisplayName("삭제된 카드는 조회하지 않는다")
		void shouldNotReturnCardWhenCardIsDeletedTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			card.delete();
			entityManager.persist(card);

			// when
			List<CardDto> cards = cardRepository.findCardByCategoryId(10, null, categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(cards).isEmpty();
		}

		@Test
		@DisplayName("가득찬 경우 항상 pageSize보다 하나 더 조회한다")
		void shouldReturnCardWhenCardIsFullTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();
			entityManager.persist(MemberTestHelper.generateMember(memberId));
			entityManager.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));
			for (int i = 0; i < 10; i++) {
				entityManager.persist(CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()));
			}
			int pageSize = 9;

			// when
			List<CardDto> cards = cardRepository.findCardByCategoryId(pageSize, null, categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(cards).hasSize(pageSize + 1);
		}

		@Test
		@DisplayName("lastCardId를 입력한 경우 lastCardId보다 같거나 작은 cardId만 조회된다")
		void shouldReturnCardWhenLastCardIdIsInputTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id memberId = Id.generateNextId();
			entityManager.persist(MemberTestHelper.generateMember(memberId));
			entityManager.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));
			Id lastCardId = Id.generateNextId();
			entityManager.persist(CardTestHelper.genOxCard(memberId, categoryId, lastCardId));
			for (int i = 0; i < 10; i++) {
				entityManager.persist(CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()));
			}
			int pageSize = 9;

			// when
			List<CardDto> cards = cardRepository.findCardByCategoryId(pageSize, lastCardId, categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(cards).hasSize(1);
		}

	}

	/**
	 * 1. 카테고리가 공유되어 있지 않은 경우 공유 카드 조회 결과는 없다
	 * 2. 카테고리가 공유되어 있고 그 내부에 카드가 존재하는 경우 카드 조회 결과는 존재한다
	 * 3. 카테고리가 공유되어 있지만 카드가 없는 경우 조회 결과는 없다
	 */
	@Nested
	@DisplayName("findCardInSharedCategory 테스트")
	class FindCardInSharedCategoryTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();

		@Test
		@DisplayName("카테고리가 공유되어 있지 않은 경우 공유 카드 조회 결과는 없다")
		void shouldReturnEmptyWhenCallFindCardInSharedCategoryTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			entityManager.persist(category);

			// when
			Optional<CardResponseDto> optional = cardRepository.findCardInSharedCategory(categoryId);

			// then
			assertThat(optional).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 공유되어 있고 그 내부에 카드가 존재하는 경우 카드 조회 결과는 존재한다")
		void shouldReturnCardWhenCallFindCardInSharedCategoryTest() {
			// given
			Id cardId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			OxCard card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card);

			// when
			Optional<CardResponseDto> optional = cardRepository.findCardInSharedCategory(cardId);

			// then
			assertThat(optional).isPresent();
		}

		@Test
		@DisplayName("카테고리가 공유되어 있지만 카드가 없는 경우 조회 결과는 없다")
		void shouldReturnEmptyWhenCallFindCardInSharedCategoryTest2() {
			// given
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			entityManager.persist(category);

			// when
			Optional<CardResponseDto> optional = cardRepository.findCardInSharedCategory(categoryId);

			// then
			assertThat(optional).isEmpty();
		}
	}

	/**
	 * 1. 카테고리 내의 카드가 존재하지 않으면 0을 리턴해야 한다
	 * 2. 카테고리 내의 카드가 존재하면 카드 개수를 리턴해야 한다
	 */
	@Nested
	@DisplayName("countCardsByCategoryId 테스트")
	class CountCardsByCategoryId {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		Id cardId = Id.generateNextId();

		@Test
		@DisplayName("카테고리 내의 카드가 존재하지 않으면 0을 리턴해야 한다")
		void shouldReturnZeroWhenCallCountCardsByCategoryIdTest() {
			// when
			long count = cardRepository.countCardsByCategoryId(categoryId);

			// then
			assertThat(count).isZero();
		}

		@Test
		@DisplayName("카테고리 내의 카드가 존재하면 카드 개수를 리턴해야 한다")
		void shouldReturnCardCountWhenCallCountCardsByCategoryIdTest() {
			// given
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card);

			// when
			long count = cardRepository.countCardsByCategoryId(categoryId);

			// then
			assertThat(count).isEqualTo(1);
		}
	}

	@Nested
	@DisplayName("countCardsByCategoryIdIsDeletedFalse 테스트")
	class CountCardsByCategoryIdIsDeletedFalseTest {

		@Test
		@DisplayName("카테고리 내의 카드가 존재하지 않으면 0을 리턴해야 한다")
		void shouldReturnZeroWhenNotCardExistTest() {
			// given
			Id categoryId = Id.generateNextId();

			// when
			Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(List.of(categoryId));

			// then
			assertThat(counts.get(categoryId)).isZero();
		}

		@Test
		@DisplayName("카테고리 내의 카드가 존재하는 카드 수만큼 리턴해야 한다")
		void shouldCountByCardCountTest() {
			// given
			Id categoryId1 = Id.generateNextId();
			Id categoryId2 = Id.generateNextId();
			Card card1 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId1, Id.generateNextId());
			Card card2 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId1, Id.generateNextId());
			Card card3 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId2, Id.generateNextId());
			entityManager.persist(card1);
			entityManager.persist(card2);
			entityManager.persist(card3);

			// when
			Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(
				List.of(categoryId1, categoryId2));

			// then
			assertThat(counts)
				.containsEntry(categoryId1, 2L)
				.containsEntry(categoryId2, 1L);
		}

		@Test
		@DisplayName("카드가 있어도 삭제된 경우 카운트하지 않는다")
		void shouldNotCountWhenCardIsDeletedTest() {
			// given
			Id categoryId = Id.generateNextId();
			Card card = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, Id.generateNextId());
			card.delete();
			entityManager.persist(card);

			// when
			Map<Id, Long> counts = cardRepository.countCardsByCategoryIdIsDeletedFalse(List.of(categoryId));

			// then
			assertThat(counts.get(categoryId)).isZero();
		}
	}

	/**
	 * 카테고리 내의 카드가 존재하지 않으면 빈 리스트를 리턴해야 한다
	 * 카드가 존재하나 히스토리가 존재하지 않는 경우에도 빈 리스트를 리턴해서는 안된다
	 * 카테고리 내의 카드가 존재하면 score가 오름차순으로 정렬된 카드 리스트를 limit 갯수만큼 리턴해야 한다
	 * 카드 히스토리가 없는 카드가 카드히스토리가 존재하는 카드보다 앞에 존재해야 함
	 * 평균 점수가 입력점수보다 낮으면 조회하지 않음
	 */
	@Nested
	@DisplayName("findCardByCategoryIdScoreAsc 테스트")
	class FindCardByCategoryIdScoreAscTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		Member member = MemberTestHelper.generateMember(memberId);

		@Test
		@DisplayName("카테고리 내의 카드가 존재하지 않으면 빈 리스트를 리턴해야 한다")
		void shouldReturnEmptyListWhenCallFindCardByCategoryIdScoreAscTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			entityManager.persist(member);
			entityManager.persist(category);

			// when
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, Score.of(100), 1);

			// then
			assertThat(cards).isEmpty();
		}

		@Test
		@DisplayName("카드가 존재하나 히스토리가 존재하지 않는 경우에도 빈 리스트를 리턴해서는 안된다")
		void shouldReturnCardListWhenCallFindCardByCategoryIdScoreAscTest() {
			// given
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card);

			// when
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, Score.of(100), 1);

			// then
			assertThat(cards).isNotEmpty();
		}

		@Test
		@DisplayName("카테고리 내의 카드가 존재하면 score가 오름차순으로 정렬된 카드 리스트를 limit 갯수만큼 리턴해야 한다")
		void shouldReturnCardListWhenCallFindCardByCategoryIdScoreAscTest2() {
			// given
			Id cardId = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, cardId2);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 30);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId2, 10);

			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card);
			entityManager.persist(card2);
			entityManager.persist(cardHistory1);
			entityManager.persist(cardHistory2);

			// when
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, Score.of(100), 1);

			// then
			assertThat(cards).hasSize(1);
			assertThat(cards.get(0).getCardId()).isEqualTo(cardId2);
		}

		@Test
		@DisplayName("카드 히스토리가 없는 카드가 카드히스토리가 존재하는 카드보다 앞에 존재해야 함")
		void shouldReturnCardListWhenCallFindCardByCategoryIdScoreAscTest3() {
			// given
			Id cardId = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, cardId2);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, 30);

			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card);
			entityManager.persist(card2);
			entityManager.persist(cardHistory1);

			// when
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, Score.of(100), 1);

			// then
			assertThat(cards).hasSize(1);
			assertThat(cards.get(0).getCardId()).isEqualTo(cardId2);
		}

		@Test
		@DisplayName("평균 점수가 입력점수보다 낮으면 조회하지 않음")
		void shouldNotFindCardWhenScoreLowerTest() {
			// given
			Id cardId = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Id cardHistoryId1 = Id.generateNextId();
			Id cardHistoryId2 = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, cardId2);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(cardHistoryId1, cardId, 30);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(cardHistoryId2, cardId2, 15);
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card);
			entityManager.persist(card2);
			entityManager.persist(cardHistory1);
			entityManager.persist(cardHistory2);

			// when
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, Score.of(10), 10);

			// then
			assertThat(cards).isEmpty();
		}
	}

	/**
	 * 1. 카드가 존재하지 않으면 빈 맵을 리턴해야 한다
	 * 2. 카드가 존재하나 memberId가 일치하지 않으면 빈 맵을 리턴해야 한다
	 */
	@Nested
	@DisplayName("findMapByListOfCardIdAndMemberId 테스트")
	class FindMapByListOfCardIdAndMemberIdTest {

		Id memberId = Id.generateNextId();
		Id categoryId = Id.generateNextId();
		Id cardId = Id.generateNextId();

		@Test
		@DisplayName("카드가 존재하지 않으면 빈 맵을 리턴해야 한다")
		void shouldReturnEmptyMapWhenCallFindMapByListOfCardIdAndMemberIdTest() {
			// given
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			entityManager.persist(member);
			entityManager.persist(category);

			// when
			Map<Id, List<Id>> cardMap = cardRepository.findMapByListOfCardIdAndMemberId(List.of(cardId), memberId);

			// then
			assertThat(cardMap).isEmpty();
		}

		@Test
		@DisplayName("카드가 존재하나 memberId가 일치하지 않으면 빈 맵을 리턴해야 한다")
		void shouldReturnEmptyMapWhenCallFindMapByListOfCardIdAndMemberIdTest2() {
			// given
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), cardId);
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card);

			// when
			Map<Id, List<Id>> cardMap = cardRepository.findMapByListOfCardIdAndMemberId(List.of(cardId),
				Id.generateNextId());

			// then
			assertThat(cardMap).isEmpty();
		}
	}

}