package com.almondia.meca.cardhistory.application.helper;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.common.domain.vo.Id;

public class CardHistoryFactory {

	public static CardHistory makeCardHistory(CardHistoryDto cardHistoryDto, Id categoryId) {
		return CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.cardId(cardHistoryDto.getCardId())
			.categoryId(categoryId)
			.userAnswer(cardHistoryDto.getUserAnswer())
			.score(cardHistoryDto.getScore())
			.build();
	}
}
