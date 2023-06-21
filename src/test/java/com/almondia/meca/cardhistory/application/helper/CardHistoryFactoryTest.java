package com.almondia.meca.cardhistory.application.helper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;

/**
 * dto로부터 정상정인 인스턴스 생성하는지 테스트
 */
class CardHistoryFactoryTest {

	@Nested
	@DisplayName("makeCardHistory 테스트")
	class MakeCardHistoryTest {

		@Test
		@DisplayName("request와 회원 아이디 그리고 score를 활용해 cardHistory를 생성해야 함")
		void shouldReturnCardHistoryUsingArguments() {
			// given
			final Id cardId = Id.generateNextId();
			final Id solvedMemberId = Id.generateNextId();
			final Answer answer = new Answer("O");
			final Score score = new Score(100);
			CardHistoryRequestDto cardHistoryRequestDto = new CardHistoryRequestDto(cardId, answer);
			Card card = CardTestHelper.genOxCard(solvedMemberId, Id.generateNextId(), cardId);

			// when
			CardHistory cardHistory = CardHistoryFactory.makeCardHistory(cardHistoryRequestDto, card, solvedMemberId,
				score);

			// then
			assertThat(cardHistory)
				.hasFieldOrProperty("cardHistoryId")
				.hasFieldOrPropertyWithValue("cardId", cardId)
				.hasFieldOrPropertyWithValue("solvedMemberId", solvedMemberId)
				.hasFieldOrPropertyWithValue("userAnswer", answer)
				.hasFieldOrPropertyWithValue("score", score)
				.hasFieldOrPropertyWithValue("cardSnapShot", CardSnapShot.copyShot(card));
		}
	}
}