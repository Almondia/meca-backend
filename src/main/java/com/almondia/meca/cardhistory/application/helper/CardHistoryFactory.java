package com.almondia.meca.cardhistory.application.helper;

import java.util.List;
import java.util.stream.Collectors;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
import com.almondia.meca.common.domain.vo.Id;

public class CardHistoryFactory {

	public static List<CardHistory> makeCardHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto,
		List<Card> cards,
		Id solvedMemberId) {
		return saveRequestCardHistoryDto.getCardHistories().stream()
			.map(cardHistoryRequestDto -> {
				Card findCard = cards.stream()
					.filter(card -> card.getCardId().equals(cardHistoryRequestDto.getCardId()))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드가 포함되어 있습니다."));
				return makeCardHistory(cardHistoryRequestDto, findCard, solvedMemberId);
			})
			.collect(Collectors.toList());
	}

	private static CardHistory makeCardHistory(CardHistoryRequestDto cardHistoryRequestDto, Card card,
		Id solvedMemberId) {
		return CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.cardId(cardHistoryRequestDto.getCardId())
			.solvedMemberId(solvedMemberId)
			.userAnswer(cardHistoryRequestDto.getUserAnswer())
			.score(cardHistoryRequestDto.getScore())
			.cardSnapShot(CardSnapShot.copyShot(card))
			.build();
	}
}
