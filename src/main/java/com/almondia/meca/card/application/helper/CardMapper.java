package com.almondia.meca.card.application.helper;

import java.util.Arrays;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;

public class CardMapper {

	public static CardResponseDto cardToDto(Card card) {
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

	public static CardResponseDto oxCardToDto(OxCard oxCard) {
		return CardResponseDto.builder()
			.cardId(oxCard.getCardId())
			.title(oxCard.getTitle())
			.question(oxCard.getQuestion())
			.categoryId(oxCard.getCategoryId())
			.cardType(oxCard.getCardType())
			.createdAt(oxCard.getCreatedAt())
			.modifiedAt(oxCard.getModifiedAt())
			.answer(oxCard.getOxAnswer().name())
			.description(oxCard.getDescription())
			.build();
	}

	public static CardResponseDto keywordCardToDto(KeywordCard keywordCard) {
		return CardResponseDto.builder()
			.cardId(keywordCard.getCardId())
			.title(keywordCard.getTitle())
			.question(keywordCard.getQuestion())
			.categoryId(keywordCard.getCategoryId())
			.cardType(keywordCard.getCardType())
			.createdAt(keywordCard.getCreatedAt())
			.modifiedAt(keywordCard.getModifiedAt())
			.answer(keywordCard.getKeywordAnswer().toString())
			.description(keywordCard.getDescription())
			.build();
	}

	public static CardResponseDto multiChoiceCardToDto(MultiChoiceCard multiChoiceCard) {
		return CardResponseDto.builder()
			.cardId(multiChoiceCard.getCardId())
			.title(multiChoiceCard.getTitle())
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
