package com.almondia.meca.helper;

import java.time.LocalDateTime;
import java.util.Random;

import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;

public class CardHistoryTestHelper {

	private static final Random random = new Random();

	public static CardHistory generateCardHistory(Id cardHistoryId, Id cardId, Id categoryId, int score) {
		return CardHistory.builder()
			.cardHistoryId(cardHistoryId)
			.cardId(cardId)
			.userAnswer(new Answer("answer"))
			.categoryId(categoryId)
			.score(new Score(score))
			.createdAt(LocalDateTime.now())
			.build();
	}
}