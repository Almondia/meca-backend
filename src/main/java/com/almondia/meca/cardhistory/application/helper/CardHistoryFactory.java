package com.almondia.meca.cardhistory.application.helper;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryResponseDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.common.domain.vo.Id;

public class CardHistoryFactory {

	public static CardHistory makeCardHistory(CardHistoryResponseDto cardHistoryResponseDto, Id categoryId) {
		return CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.cardId(cardHistoryResponseDto.getCardId())
			.categoryId(categoryId)
			.userAnswer(cardHistoryResponseDto.getUserAnswer())
			.score(cardHistoryResponseDto.getScore())
			.build();
	}
}
