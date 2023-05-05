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

import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;

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
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, categoryId,
				10);
			cardHistory.delete();
			em.persist(cardHistory);

			// when
			CursorPage<CardHistoryDto> result = cardHistoryRepository.findCardHistoriesByCardId(cardId,
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
			CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId, categoryId,
				10);
			em.persist(cardHistory);

			// expect
			assertThatThrownBy(() -> cardHistoryRepository.findCardHistoriesByCardId(cardId, 1001, null))
				.isInstanceOf(AssertionError.class);
		}

		@Test
		@DisplayName("lastCardHistoryId가 null이 아니면 해당 인덱스부터 조회한다")
		void shouldFindCardHistoryFromLastCardHistoryIdTest() {
			// given
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId,
				categoryId,
				10);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId,
				categoryId,
				10);
			CardHistory cardHistory3 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId,
				categoryId,
				10);
			em.persist(cardHistory1);
			em.persist(cardHistory2);
			em.persist(cardHistory3);

			// when
			CursorPage<CardHistoryDto> result = cardHistoryRepository.findCardHistoriesByCardId(cardId,
				10, cardHistory2.getCardHistoryId());

			// then
			assertThat(result.getContents()).hasSize(2);
			assertThat(result.getHasNext()).isNull();
		}

	}

}