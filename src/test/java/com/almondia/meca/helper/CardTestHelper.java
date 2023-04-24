package com.almondia.meca.helper;

import java.time.LocalDateTime;

import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

public class CardTestHelper {

	public static OxCard genOxCard(Id memberId, Id categoryId, Id cardId) {
		return OxCard.builder()
			.cardId(cardId)
			.categoryId(categoryId)
			.memberId(memberId)
			.title(new Title("title"))
			.description(new Description("description"))
			.question(new Question("question"))
			.oxAnswer(OxAnswer.O)
			.cardType(CardType.OX_QUIZ)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.isDeleted(false)
			.build();
	}

	public static OxCard genDeletedOxCard(Id memberId, Id categoryId, Id cardId) {
		return OxCard.builder()
			.cardId(cardId)
			.categoryId(categoryId)
			.memberId(memberId)
			.title(new Title("title"))
			.description(new Description("description"))
			.question(new Question("question"))
			.oxAnswer(OxAnswer.O)
			.cardType(CardType.OX_QUIZ)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.isDeleted(true)
			.build();
	}
}
