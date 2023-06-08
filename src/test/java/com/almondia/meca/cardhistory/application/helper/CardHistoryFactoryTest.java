package com.almondia.meca.cardhistory.application.helper;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;

/**
 * dto로부터 정상정인 인스턴스 생성하는지 테스트
 */
class CardHistoryFactoryTest {

	@Test
	@DisplayName("dto에서 정상정인 인스턴스 생성하는지 테스트")
	void shouldGenerateNewInstanceFromDtoTest() {
		Id memberId = Id.generateNextId();
		Id categoryId = Id.generateNextId();
		Id cardId = Id.generateNextId();
		Id solvedMemberId = Id.generateNextId();

		CardHistoryRequestDto cardHistoryRequestDto = CardHistoryRequestDto.builder()
			.cardId(cardId)
			.score(new Score(100))
			.userAnswer(new Answer("123"))
			.build();

		Card card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
		SaveRequestCardHistoryDto saveRequestCardHistoryDto = SaveRequestCardHistoryDto.builder()
			.cardHistories(List.of(cardHistoryRequestDto))
			.build();

		List<CardHistory> result = CardHistoryFactory.makeCardHistories(saveRequestCardHistoryDto, List.of(card),
			solvedMemberId);
		assertThat(result.get(0))
			.hasFieldOrProperty("cardHistoryId")
			.hasFieldOrPropertyWithValue("cardId", cardHistoryRequestDto.getCardId())
			.hasFieldOrPropertyWithValue("score", cardHistoryRequestDto.getScore())
			.hasFieldOrPropertyWithValue("userAnswer", cardHistoryRequestDto.getUserAnswer())
			.hasFieldOrPropertyWithValue("solvedMemberId", solvedMemberId)
			.hasFieldOrPropertyWithValue("isDeleted", false)
			.hasFieldOrProperty("cardSnapShot");

	}
}