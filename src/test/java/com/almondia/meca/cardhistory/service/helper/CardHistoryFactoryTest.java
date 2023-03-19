package com.almondia.meca.cardhistory.service.helper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
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
		CardHistoryDto cardHistoryDto = CardHistoryDto.builder()
			.cardId(Id.generateNextId())
			.score(new Score(100))
			.userAnswer(new Answer("123"))
			.build();
		Id categoryId = Id.generateNextId();
		CardHistory cardHistory = CardHistoryFactory.makeCardHistory(cardHistoryDto, categoryId);
		assertThat(cardHistory)
			.hasFieldOrPropertyWithValue("cardId", cardHistoryDto.getCardId())
			.hasFieldOrPropertyWithValue("score", cardHistoryDto.getScore())
			.hasFieldOrPropertyWithValue("userAnswer", cardHistoryDto.getUserAnswer())
			.hasFieldOrPropertyWithValue("categoryId", categoryId);

	}
}