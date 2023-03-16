package com.almondia.meca.card.sevice.helper;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;

public class CardMapper {

	public static CardResponseDto oxCardToDto(OxCard oxCard) {
		return CardResponseDto.builder()
			.cardId(oxCard.getCardId())
			.title(oxCard.getTitle())
			.question(oxCard.getQuestion())
			.images(oxCard.getImages())
			.categoryId(oxCard.getCategoryId())
			.cardType(oxCard.getCardType())
			.isDeleted(oxCard.isDeleted())
			.createdAt(oxCard.getCreatedAt())
			.modifiedAt(oxCard.getModifiedAt())
			.answer(oxCard.getOxAnswer().name())
			.build();
	}

	public static CardResponseDto keywordCardToDto(KeywordCard keywordCard) {
		return CardResponseDto.builder()
			.cardId(keywordCard.getCardId())
			.title(keywordCard.getTitle())
			.question(keywordCard.getQuestion())
			.images(keywordCard.getImages())
			.categoryId(keywordCard.getCategoryId())
			.cardType(keywordCard.getCardType())
			.isDeleted(keywordCard.isDeleted())
			.createdAt(keywordCard.getCreatedAt())
			.modifiedAt(keywordCard.getModifiedAt())
			.answer(keywordCard.getKeywordAnswer().toString())
			.build();
	}

	public static CardResponseDto multiChoiceCardToDto(MultiChoiceCard multiChoiceCard) {
		return CardResponseDto.builder()
			.cardId(multiChoiceCard.getCardId())
			.title(multiChoiceCard.getTitle())
			.question(multiChoiceCard.getQuestion())
			.images(multiChoiceCard.getImages())
			.categoryId(multiChoiceCard.getCategoryId())
			.cardType(multiChoiceCard.getCardType())
			.isDeleted(multiChoiceCard.isDeleted())
			.createdAt(multiChoiceCard.getCreatedAt())
			.modifiedAt(multiChoiceCard.getModifiedAt())
			.answer(multiChoiceCard.getMultiChoiceAnswer().toString())
			.build();
	}
}
