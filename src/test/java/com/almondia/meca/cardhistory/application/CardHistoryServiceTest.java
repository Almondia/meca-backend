package com.almondia.meca.cardhistory.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.cardhistory.domain.service.MorphemeAnalyzer;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;
import com.querydsl.jpa.impl.JPAQueryFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class CardHistoryServiceTest {

	@Autowired
	JPAQueryFactory queryFactory;

	@Autowired
	CardHistoryRepository cardHistoryRepository;

	@Autowired
	CardRepository cardRepository;

	@Autowired
	EntityManager em;

	MorphemeAnalyzer morphemeAnalyzer;
	CardHistoryService cardHistoryService;

	@BeforeEach
	void setUp() {
		morphemeAnalyzer = Mockito.mock(MorphemeAnalyzer.class);
		cardHistoryService = new CardHistoryService(cardHistoryRepository, cardRepository, morphemeAnalyzer);
	}

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
			em.persist(card1);

			// expect
			assertThatThrownBy(
				() -> cardHistoryService.saveCardHistory(
					new CardHistoryRequestDto(Id.generateNextId(), new Answer("O")), memberId))
				.isInstanceOf(IllegalArgumentException.class);

		}

		@Test
		@DisplayName("등록 성공한 경우 저장 여부 테스트")
		void saveCardHistoriesTest() {
			// given
			final Id memberId = Id.generateNextId();
			final Id categoryId = Id.generateNextId();
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId());
			em.persist(card1);

			// when
			cardHistoryService.saveCardHistory(
				CardHistoryRequestDto.builder()
					.cardId(card1.getCardId())
					.userAnswer(new Answer("O"))
					.build()
				, memberId);

			// then
			QCardHistory cardHistory = QCardHistory.cardHistory;
			Long fetch = queryFactory.select(cardHistory.count())
				.from(cardHistory)
				.fetchOne();
			assertThat(fetch).isEqualTo(1L);
		}
	}

	@Nested
	@DisplayName("카드ID 기반 카드 히스토리 조회")
	class FindCardHistoriesByCardIdTest {

		@Test
		@DisplayName("존재하는 카드 ID로 조회 요청한 경우 조회 결과 반환")
		void findCardHistoriesByCardIdTest() {
			// given
			final int pageSize = 1;
			final Id cardId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final Id categoryId = Id.generateNextId();
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			em.persist(card1);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> cursor = cardHistoryService.findCardHistoriesByCardId(
				cardId, pageSize, null);

			// then
			assertThat(cursor.getContents()).isNotNull();
		}
	}

	@Nested
	@DisplayName("문제푼회원ID기반 카드 히스토리 조회")
	class FindCardHistoriesBySolvedMemberIdTest {

		@Test
		@DisplayName("존재하는 문제푼회원 ID로 조회 요청한 경우 조회 결과 반환")
		void findCardHistoriesBySolvedMemberIdTest() {
			// given
			final int pageSize = 1;
			final Id cardId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final Id categoryId = Id.generateNextId();
			Card card1 = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			em.persist(card1);

			// when
			CursorPage<CardHistoryWithCardAndMemberResponseDto> cursor = cardHistoryService.findCardHistoriesBySolvedMemberId(
				memberId, pageSize, null);

			// then
			assertThat(cursor.getContents()).isNotNull();
		}
	}
}