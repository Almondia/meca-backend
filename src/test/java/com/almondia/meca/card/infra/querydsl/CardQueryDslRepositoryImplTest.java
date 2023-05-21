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

import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardCursorPageWithSharedCategoryDto;
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
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
	 * 1. 페이징 사이즈보다 적게 조회한 경우 hasNext는 null이다
	 * 2. 다음 페이지가 존재하는 경우 hasNext 값은 존재한다
	 * 3. 삭제된 카드는 조회하지 않는다
	 * 4. lastCardId를 입력한 경우 lastCardId보다 같거나 작은 cardId만 조회된다
	 * 5. 검색 옵션의 containTitle 속성이 존재하는 경우, 카드 제목에 containTitle이 포함된 카드만 조회된다
	 */
	@Nested
	@DisplayName("findCardByCategoryIdUsingCursorPaging 테스트")
	class FindCardByCategoryIdUsingCursorPagingTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();

		@Test
		@DisplayName("페이징 사이즈보다 적게 조회한 경우 hasNext는 null이다")
		void shouldReturnNullWhenCallFindCardByCategoryIdUsingCursorPagingTest() {
			// given
			int pageSize = 5;

			// when
			CardCursorPageWithCategory page = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize, null,
				categoryId, CardSearchOption.builder().build());

			// then
			assertThat(page.getHasNext()).isNull();
		}

		@Test
		@DisplayName("다음 페이지가 존재하는 경우 hasNext 값은 존재한다")
		void shouldReturnHasNextWhenCallFindCardByCategoryIdUsingCursorPagingTest() {
			// given
			int pageSize = 1;
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);

			// when
			CardCursorPageWithCategory page = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize, null,
				categoryId, CardSearchOption.builder().build());

			// then
			assertThat(page.getHasNext()).isNotNull();
		}

		@Test
		@DisplayName("삭제된 카드는 조회하지 않는다")
		void shouldNotReturnDeletedCardWhenCallFindCardByCategoryIdUsingCursorPagingTest() {
			// given
			int pageSize = 1;
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			card1.delete();
			entityManager.persist(category);
			entityManager.persist(card1);

			// when
			CardCursorPageWithCategory page = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize, null,
				categoryId, CardSearchOption.builder().build());

			// then
			assertThat(page.getHasNext()).isNull();
			assertThat(page.getContents()).isEmpty();
		}

		@Test
		@DisplayName("lastCardId를 입력한 경우 lastCardId보다 같거나 작은 cardId만 조회된다")
		void shouldReturnCardIdLessThanLastCardIdWhenCallFindCardByCategoryIdUsingCursorPagingTest() {
			// given
			int pageSize = 3;
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);

			// when
			CardCursorPageWithCategory page = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize,
				card2.getCardId(),
				categoryId, CardSearchOption.builder().build());

			// then
			assertThat(page.getHasNext()).isNull();
			assertThat(page.getContents()).hasSize(2);
		}

		@Test
		@DisplayName("검색 옵션의 containTitle 속성이 존재하는 경우, 카드 제목에 containTitle이 포함된 카드만 조회된다")
		void shouldReturnCardTitleContainsContainTitleWhenCallFindCardByCategoryIdUsingCursorPagingTest() {
			// given
			int pageSize = 3;
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card3 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			card1.changeTitle(new Title("title1"));
			card2.changeTitle(new Title("title2"));
			card3.changeTitle(new Title("title3"));
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);
			entityManager.persist(card3);

			// when
			CardCursorPageWithCategory page = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize, null,
				categoryId, CardSearchOption.builder().containTitle("title").build());

			// then
			assertThat(page.getHasNext()).isNull();
			assertThat(page.getContents()).hasSize(3);
		}
	}

	/**
	 * 1. 페이징 사이즈보다 적게 조회한 경우 hasNext는 null이다
	 * 2. 다음 페이지가 존재하는 경우 hasNext 값은 존재한다
	 * 3. 조회시 회원 정보가 있어야 한다
	 * 4. 삭제된 카드는 조회하면 안된다
	 * 5. 공유되지 않은 카테고리 내부의 카드는 조회하면 안된다
	 * 6. 카테고리가 공유되어 있고 그 내부에 카드가 존재하는 경우 카드 조회 결과는 존재한다
	 * 7. 카테고리가 공유되어 있지만 카드가 없는 경우 조회 결과는 없다
	 * 8. lastCardId를 입력한 경우 lastCardId보다 같거나 작은 cardId만 조회된다
	 * 9. 검색 옵션의 containTitle 속성이 존재하는 경우, 카드 제목에 containTitle이 포함된 카드만 조회된다
	 */
	@Nested
	@DisplayName("findCardBySharedCategoryCursorPaging 테스트")
	class FindCardBySharedCategoryCursorPagingTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();

		@Test
		@DisplayName("페이징 사이즈보다 적게 조회한 경우 hasNext는 null이다")
		void shouldReturnNullWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 5;

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page.getHasNext()).isNull();
		}

		@Test
		@DisplayName("다음 페이지가 존재하는 경우 hasNext 값은 존재한다")
		void shouldReturnHasNextWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 1;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page.getHasNext()).isNotNull();
		}

		@Test
		@DisplayName("조회시 회원 정보가 있어야 한다")
		void shouldReturnMemberAndCategoryWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 1;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page).isNotNull();
			assertThat(page.getContents().get(0).getMemberInfo()).isNotNull();
		}

		@Test
		@DisplayName("삭제된 카드는 조회하면 안된다")
		void shouldNotReturnDeletedCardWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 1;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			card1.delete();
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page).isNotNull();
			assertThat(page.getContents()).hasSize(1);
		}

		@Test
		@DisplayName("공유되지 않은 카테고리 내부의 카드는 조회하면 안된다")
		void shouldNotReturnCardWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 1;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page).isNotNull();
			assertThat(page.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 공유되어 있고 그 내부에 카드가 존재하는 경우 카드 조회 결과는 존재한다")
		void shouldReturnCardWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 2;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page).isNotNull();
			assertThat(page.getContents()).hasSize(2);
		}

		@Test
		@DisplayName("카테고리가 공유되어 있지만 카드가 없는 경우 조회 결과는 없다")
		void shouldReturnEmptyWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 2;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			entityManager.persist(member);
			entityManager.persist(category);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page).isNotNull();
			assertThat(page.getContents()).isEmpty();
		}

		@Test
		@DisplayName("lastCardId를 입력한 경우 lastCardId보다 같거나 작은 cardId만 조회된다")
		void shouldReturnCardsWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 3;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card3 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);
			entityManager.persist(card3);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				card3.getCardId(),
				categoryId,
				CardSearchOption.builder().build());

			// then
			assertThat(page).isNotNull();
			assertThat(page.getContents()).hasSize(3);
		}

		@Test
		@DisplayName("검색 옵션의 containTitle 속성이 존재하는 경우, 카드 제목에 containTitle이 포함된 카드만 조회된다")
		void shouldReturnCardContainTitleWhenCallFindCardBySharedCategoryCursorPagingTest() {
			// given
			int pageSize = 3;
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			OxCard card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			OxCard card3 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			entityManager.persist(member);
			entityManager.persist(category);
			entityManager.persist(card1);
			entityManager.persist(card2);
			entityManager.persist(card3);

			// when
			CardCursorPageWithSharedCategoryDto page = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
				null,
				categoryId,
				CardSearchOption.builder().containTitle("title").build());

			// then
			assertThat(page).isNotNull();
			assertThat(page.getContents()).hasSize(3);
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
			Optional<SharedCardResponseDto> optional = cardRepository.findCardInSharedCategory(categoryId);

			// then
			assertThat(optional.isEmpty()).isTrue();
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
			Optional<SharedCardResponseDto> optional = cardRepository.findCardInSharedCategory(cardId);

			// then
			assertThat(optional.isPresent()).isTrue();
		}

		@Test
		@DisplayName("카테고리가 공유되어 있지만 카드가 없는 경우 조회 결과는 없다")
		void shouldReturnEmptyWhenCallFindCardInSharedCategoryTest2() {
			// given
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			entityManager.persist(category);

			// when
			Optional<SharedCardResponseDto> optional = cardRepository.findCardInSharedCategory(categoryId);

			// then
			assertThat(optional.isEmpty()).isTrue();
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
			assertThat(count).isEqualTo(0);
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

	/**
	 * 1. 카테고리 내의 카드가 존재하지 않으면 빈 리스트를 리턴해야 한다
	 * 2. 카드가 존재하나 히스토리가 존재하지 않는 경우에도 빈 리스트를 리턴해서는 안된다
	 * 3. 카테고리 내의 카드가 존재하면 score가 오름차순으로 정렬된 카드 리스트를 limit 갯수만큼 리턴해야 한다
	 * 4. 카드 히스토리가 없는 카드가 카드히스토리가 존재하는 카드보다 앞에 존재해야 함
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
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, 1);

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
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, 1);

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
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, 1);

			// then
			assertThat(cards).isNotEmpty();
			assertThat(cards.size()).isEqualTo(1);
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
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, 1);

			// then
			assertThat(cards).isNotEmpty();
			assertThat(cards.size()).isEqualTo(1);
			assertThat(cards.get(0).getCardId()).isEqualTo(cardId2);
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