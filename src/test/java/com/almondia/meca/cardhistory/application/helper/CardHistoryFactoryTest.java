package com.almondia.meca.cardhistory.application.helper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryResponseDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;

/**
 * dto로부터 정상정인 인스턴스 생성하는지 테스트
 */
class CardHistoryFactoryTest {

	@Test
	@DisplayName("dto에서 정상정인 인스턴스 생성하는지 테스트")
	void shouldGenerateNewInstanceFromDtoTest() {
		CardHistoryResponseDto cardHistoryResponseDto = CardHistoryResponseDto.builder()
			.cardId(Id.generateNextId())
			.score(new Score(100))
			.userAnswer(new Answer("123"))
			.build();
		Id categoryId = Id.generateNextId();
		CardHistory cardHistory = CardHistoryFactory.makeCardHistory(cardHistoryResponseDto, categoryId);
		assertThat(cardHistory)
			.hasFieldOrPropertyWithValue("cardId", cardHistoryResponseDto.getCardId())
			.hasFieldOrPropertyWithValue("score", cardHistoryResponseDto.getScore())
			.hasFieldOrPropertyWithValue("userAnswer", cardHistoryResponseDto.getUserAnswer())
			.hasFieldOrPropertyWithValue("categoryId", categoryId);

	}
}