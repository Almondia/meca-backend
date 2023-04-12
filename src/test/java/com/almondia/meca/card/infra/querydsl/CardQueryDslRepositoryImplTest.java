package com.almondia.meca.card.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

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
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
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
	 */
	@Nested
	@DisplayName("findCardByCategoryIdUsingCursorPaging 테스트")
	class FindCardByCategoryIdUsingCursorPagingTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();

		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.eqCategoryId(categoryId)
			.build();
		SortOption<CardSortField> sortOption = new SortOption<>(CardSortField.CREATED_AT, SortOrder.DESC);

		@Test
		@DisplayName("페이징 사이즈보다 적게 조회한 경우 hasNext는 null이다")
		void shouldReturnNullWhenCallFindCardByCategoryIdUsingCursorPagingTest() {
			// given
			int pageSize = 5;

			// when
			CardCursorPageWithCategory page = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize, criteria,
				sortOption);

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
			CardCursorPageWithCategory page = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize, criteria,
				sortOption);

			// then
			assertThat(page.getHasNext()).isNotNull();
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

}