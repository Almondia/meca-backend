package com.almondia.meca.helper;

import java.time.LocalDateTime;

import com.almondia.meca.card.domain.entity.EssayCard;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.EssayAnswer;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
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

	public static KeywordCard genKeywordCard(Id memberId, Id categoryId, Id cardId) {
		return KeywordCard.builder()
			.cardId(cardId)
			.categoryId(categoryId)
			.memberId(memberId)
			.title(new Title("title"))
			.description(new Description("description"))
			.question(new Question("question"))
			.keywordAnswer(KeywordAnswer.valueOf("keyword"))
			.cardType(CardType.KEYWORD)
			.isDeleted(false)
			.build();
	}

	public static MultiChoiceCard genMultiChoiceCard(Id memberId, Id categoryId, Id cardId) {
		return MultiChoiceCard.builder()
			.cardId(cardId)
			.categoryId(categoryId)
			.memberId(memberId)
			.title(new Title("title"))
			.description(new Description("description"))
			.question(new Question("question"))
			.multiChoiceAnswer(MultiChoiceAnswer.valueOf("1"))
			.cardType(CardType.MULTI_CHOICE)
			.isDeleted(false)
			.build();
	}

	public static EssayCard genEssayCard(Id memberId, Id categoryId, Id cardId) {
		return EssayCard.builder()
			.cardId(cardId)
			.categoryId(categoryId)
			.memberId(memberId)
			.title(new Title("title"))
			.description(new Description("description"))
			.question(new Question("question"))
			.essayAnswer(EssayAnswer.valueOf("essay"))
			.cardType(CardType.ESSAY)
			.isDeleted(false)
			.build();
	}
}
