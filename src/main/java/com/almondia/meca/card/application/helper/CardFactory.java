package com.almondia.meca.card.application.helper;

import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.EssayCard;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.EssayAnswer;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceQuestion;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.common.domain.vo.Id;

public class CardFactory {

	private CardFactory() {
		throw new IllegalArgumentException("util class 입니다 ");
	}

	public static Card genCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		CardType cardType = saveCardRequestDto.getCardType();
		if (cardType.equals(CardType.OX_QUIZ)) {
			return genOxCard(saveCardRequestDto, memberId);
		}
		if (cardType.equals(CardType.KEYWORD)) {
			return genKeywordCard(saveCardRequestDto, memberId);
		}
		if (cardType.equals(CardType.MULTI_CHOICE)) {
			return genMultiChoiceCard(saveCardRequestDto, memberId);
		}
		if (cardType.equals(CardType.ESSAY)) {
			return genEssayCard(saveCardRequestDto, memberId);
		}
		throw new IllegalArgumentException("잘못된 cardType 입니다");
	}

	private static OxCard genOxCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		Description description = saveCardRequestDto.getDescription();
		return OxCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(Question.of(saveCardRequestDto.getQuestion()))
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.oxAnswer(OxAnswer.valueOf(answer.toUpperCase()))
			.description(description)
			.build();
	}

	private static KeywordCard genKeywordCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		Description description = saveCardRequestDto.getDescription();
		return KeywordCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(Question.of(saveCardRequestDto.getQuestion()))
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.description(description)
			.keywordAnswer(new KeywordAnswer(answer))
			.build();
	}

	private static MultiChoiceCard genMultiChoiceCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		Description description = saveCardRequestDto.getDescription();
		return MultiChoiceCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(MultiChoiceQuestion.of(saveCardRequestDto.getQuestion()))
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.description(description)
			.multiChoiceAnswer(new MultiChoiceAnswer(Integer.parseInt(answer)))
			.build();
	}

	private static Card genEssayCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		Description description = saveCardRequestDto.getDescription();
		return EssayCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(Question.of(saveCardRequestDto.getQuestion()))
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.essayAnswer(EssayAnswer.valueOf(answer))
			.description(description)
			.build();
	}
}
