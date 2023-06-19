package com.almondia.meca.helper;

import java.time.LocalDateTime;
import java.util.Random;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;

public class CardHistoryTestHelper {

	private static final Random random = new Random();

	public static CardHistory generateCardHistory(Id cardHistoryId, Id cardId, int score) {
		return CardHistory.builder()
			.cardHistoryId(cardHistoryId)
			.cardId(cardId)
			.solvedMemberId(Id.generateNextId())
			.userAnswer(new Answer("answer"))
			.score(new Score(score))
			.createdAt(LocalDateTime.now())
			.cardSnapShot(makeCardSnapShot())
			.build();
	}

	public static CardHistory generateCardHistory(Id cardId, Id solvedUserId) {
		return CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.cardId(cardId)
			.solvedMemberId(solvedUserId)
			.userAnswer(new Answer("answer"))
			.score(new Score(random.nextInt(100)))
			.createdAt(LocalDateTime.now())
			.cardSnapShot(makeCardSnapShot())
			.build();
	}

	public static CardHistoryDto generateCardHistoryResponseDto() {
		return CardHistoryDto.builder()
			.cardHistoryId(Id.generateNextId())
			.userAnswer(new Answer("answer"))
			.score(new Score(random.nextInt(100)))
			.createdAt(LocalDateTime.now())
			.build();
	}

	private static CardSnapShot makeCardSnapShot() {
		return CardSnapShot.builder()
			.memberId(Id.generateNextId())
			.title(Title.of("title"))
			.question(Question.of("question"))
			.answer("O")
			.cardType(CardType.OX_QUIZ)
			.description(Description.of("description"))
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}
}
