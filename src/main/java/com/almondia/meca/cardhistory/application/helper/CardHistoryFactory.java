package com.almondia.meca.cardhistory.application.helper;

import java.util.List;
import java.util.stream.Collectors;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.common.domain.vo.Id;

public class CardHistoryFactory {

	public static List<CardHistory> makeCardHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto,
		Id solvedMemberId) {
		return saveRequestCardHistoryDto.getCardHistories().stream()
			.map(cardHistoryRequestDto -> makeCardHistory(cardHistoryRequestDto, solvedMemberId))
			.collect(Collectors.toList());
	}

	private static CardHistory makeCardHistory(CardHistoryRequestDto cardHistoryRequestDto, Id solvedMemberId) {
		return CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.cardId(cardHistoryRequestDto.getCardId())
			.solvedMemberId(solvedMemberId)
			.userAnswer(cardHistoryRequestDto.getUserAnswer())
			.score(cardHistoryRequestDto.getScore())
			.build();
	}
}
