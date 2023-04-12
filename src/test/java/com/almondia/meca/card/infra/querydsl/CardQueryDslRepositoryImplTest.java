package com.almondia.meca.card.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
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

	// @Test
	// @DisplayName("findSharedCard 공유 카드 단일 조회")
	// void test2() {
	// 	Id cardId = Id.generateNextId();
	// 	Id memberId = Id.generateNextId();
	// 	Id categoryId = Id.generateNextId();
	//
	// 	memberRepository.save(Member.builder()
	// 		.memberId(memberId)
	// 		.email(new Email("email@naver.com"))
	// 		.name(new Name("name"))
	// 		.oAuthType(OAuthType.KAKAO)
	// 		.oauthId("1234")
	// 		.role(Role.USER)
	// 		.build());
	//
	// 	categoryRepository.save(Category.builder()
	// 		.categoryId(categoryId)
	// 		.memberId(memberId)
	// 		.title(new com.almondia.meca.category.domain.vo.Title("title"))
	// 		.isDeleted(false)
	// 		.isShared(true)
	// 		.build());
	//
	// 	cardRepository.save(OxCard.builder()
	// 		.memberId(memberId)
	// 		.categoryId(categoryId)
	// 		.cardId(cardId)
	// 		.oxAnswer(OxAnswer.O)
	// 		.title(new Title("title"))
	// 		.question(new Question("question"))
	// 		.description(new Description("description"))
	// 		.build());
	//
	// 	SharedCardResponseDto sharedCard = cardRepository.findSharedCard(cardId).orElseThrow();
	// 	assertThat(sharedCard.getCardInfo()).isNotNull();
	// 	assertThat(sharedCard.getMemberInfo()).isNotNull();
	// }

}