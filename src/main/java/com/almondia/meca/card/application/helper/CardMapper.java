package com.almondia.meca.card.application.helper;

import java.util.Arrays;

import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;

public class CardMapper {

	public static CardDto cardToDto(Card card) {
		if (card.getCardType().equals(CardType.OX_QUIZ)) {
			return oxCardToDto((OxCard)card);
		}
		if (card.getCardType().equals(CardType.KEYWORD)) {
			return keywordCardToDto((KeywordCard)card);
		}
		if (card.getCardType().equals(CardType.MULTI_CHOICE)) {
			return multiChoiceCardToDto((MultiChoiceCard)card);
		}
		throw new IllegalArgumentException(String.format("카드 타입은 %s 만 가능합니다", Arrays.toString(CardType.values())));
	}

	public static CardDto oxCardToDto(OxCard oxCard) {
		return CardDto.builder()
			.cardId(oxCard.getCardId())
			.title(oxCard.getTitle())
			.memberId(oxCard.getMemberId())
			.question(oxCard.getQuestion())
			.categoryId(oxCard.getCategoryId())
			.cardType(oxCard.getCardType())
			.createdAt(oxCard.getCreatedAt())
			.modifiedAt(oxCard.getModifiedAt())
			.answer(oxCard.getOxAnswer().name())
			.description(oxCard.getDescription())
			.build();
	}

	public static CardDto keywordCardToDto(KeywordCard keywordCard) {
		return CardDto.builder()
			.cardId(keywordCard.getCardId())
			.title(keywordCard.getTitle())
			.memberId(keywordCard.getMemberId())
			.question(keywordCard.getQuestion())
			.categoryId(keywordCard.getCategoryId())
			.cardType(keywordCard.getCardType())
			.createdAt(keywordCard.getCreatedAt())
			.modifiedAt(keywordCard.getModifiedAt())
			.answer(keywordCard.getKeywordAnswer().toString())
			.description(keywordCard.getDescription())
			.build();
	}

	public static CardDto multiChoiceCardToDto(MultiChoiceCard multiChoiceCard) {
		return CardDto.builder()
			.cardId(multiChoiceCard.getCardId())
			.title(multiChoiceCard.getTitle())
			.memberId(multiChoiceCard.getMemberId())
			.question(multiChoiceCard.getQuestion())
			.categoryId(multiChoiceCard.getCategoryId())
			.cardType(multiChoiceCard.getCardType())
			.createdAt(multiChoiceCard.getCreatedAt())
			.modifiedAt(multiChoiceCard.getModifiedAt())
			.answer(multiChoiceCard.getMultiChoiceAnswer().toString())
			.description(multiChoiceCard.getDescription())
			.build();
	}
}
