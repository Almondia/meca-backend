package com.almondia.meca.cardhistory.application.helper;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;

public class CardHistoryFactory {

	public static CardHistory makeCardHistory(CardHistoryRequestDto cardHistoryRequestDto, Card card, Id solvedMemberId,
		Score score) {
		return CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.cardId(cardHistoryRequestDto.getCardId())
			.solvedMemberId(solvedMemberId)
			.userAnswer(cardHistoryRequestDto.getUserAnswer())
			.score(score)
			.cardSnapShot(CardSnapShot.copyShot(card))
			.build();
	}
}
