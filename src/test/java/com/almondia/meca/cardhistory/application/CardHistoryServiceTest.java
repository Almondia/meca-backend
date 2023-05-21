package com.almondia.meca.cardhistory.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;
import com.querydsl.jpa.impl.JPAQueryFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class, CardHistoryService.class})
class CardHistoryServiceTest {

	@Autowired
	JPAQueryFactory queryFactory;

	@Autowired
	CardHistoryService cardHistoryService;

	@Autowired
	EntityManager em;

	@Nested
	@DisplayName("카드 히스토리 등록 테스트")
	class SaveCardHistoriesTest {

		@Test
		@DisplayName("존재하지 않는 카드 id로 카드 히스토리 저장을 요청한 경우 예외 발생")
		void saveCardHistoriesWithNonExistCardIdTest() {
			// given
			final Id memberId = Id.generateNextId();
			final Id categoryId = Id.generateNextId();
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			Card card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			em.persist(card1);
			em.persist(card2);

			// expect
			assertThatThrownBy(
				() -> cardHistoryService.saveCardHistories(
					makeSaveRequest(card1.getCardId(), Id.generateNextId()),
					memberId)).isInstanceOf(IllegalArgumentException.class);

		}

		@Test
		@DisplayName("등록 성공한 경우 저장 여부 테스트")
		void saveCardHistoriesTest() {
			// given
			final Id memberId = Id.generateNextId();
			final Id categoryId = Id.generateNextId();
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			Card card2 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			em.persist(card1);
			em.persist(card2);

			// when
			cardHistoryService.saveCardHistories(
				makeSaveRequest(card1.getCardId(), card2.getCardId()), memberId);

			// then
			QCardHistory cardHistory = QCardHistory.cardHistory;
			Long fetch = queryFactory.select(cardHistory.count())
				.from(cardHistory)
				.fetchOne();
			assertThat(fetch).isEqualTo(2L);
		}

		private SaveRequestCardHistoryDto makeSaveRequest(Id cardId1, Id cardId2) {
			return SaveRequestCardHistoryDto.builder()
				.cardHistories(List.of(CardHistoryRequestDto.builder()
					.cardId(cardId1)
					.userAnswer(new Answer(OxAnswer.O.toString()))
					.score(new Score(100))
					.build(), CardHistoryRequestDto.builder()
					.cardId(cardId2)
					.userAnswer(new Answer(OxAnswer.X.toString()))
					.score(new Score(0))
					.build()))
				.build();
		}
	}
}