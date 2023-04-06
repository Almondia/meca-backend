package com.almondia.meca.card.application.helper;

import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.EditText;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.common.domain.vo.Id;

public class CardFactory {

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
		throw new IllegalArgumentException("잘못된 cardType 입니다");
	}

	private static OxCard genOxCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		EditText editText = saveCardRequestDto.getEditText();
		return OxCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.oxAnswer(OxAnswer.valueOf(answer.toUpperCase()))
			.editText(editText)
			.build();
	}

	private static KeywordCard genKeywordCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		EditText editText = saveCardRequestDto.getEditText();
		return KeywordCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.editText(editText)
			.keywordAnswer(new KeywordAnswer(answer))
			.build();
	}

	private static MultiChoiceCard genMultiChoiceCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		EditText editText = saveCardRequestDto.getEditText();
		return MultiChoiceCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.editText(editText)
			.multiChoiceAnswer(new MultiChoiceAnswer(Integer.parseInt(answer)))
			.build();
	}
}
