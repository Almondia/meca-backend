package com.almondia.meca.card.sevice.helper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Image;
import com.almondia.meca.common.domain.vo.Id;

public class CardFactory {

	private static final String SPLIT_WORD = ",";

	public static Card genCard(SaveCardRequestDto saveCardRequestDto) {
		CardType cardType = saveCardRequestDto.getCardType();
		if (cardType.equals(CardType.OX_QUIZ)) {
			return genOxCard(saveCardRequestDto);
		}
		if (cardType.equals(CardType.KEYWORD)) {
			return genKeywordCard(saveCardRequestDto);
		}
		if (cardType.equals(CardType.MULTI_CHOICE)) {
			return genMultiChoiceCard(saveCardRequestDto);
		}
		throw new IllegalArgumentException("잘못된 cardType 입니다");
	}

	private static OxCard genOxCard(SaveCardRequestDto saveCardRequestDto) {
		return OxCard.builder()
			.cardId(Id.generateNextId())
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.images(makeImages(saveCardRequestDto.getImages()))
			.oxAnswer(saveCardRequestDto.getOxAnswer())
			.build();
	}

	private static KeywordCard genKeywordCard(SaveCardRequestDto saveCardRequestDto) {
		return KeywordCard.builder()
			.cardId(Id.generateNextId())
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.images(makeImages(saveCardRequestDto.getImages()))
			.keywordAnswer(saveCardRequestDto.getKeywordAnswer())
			.build();
	}

	private static MultiChoiceCard genMultiChoiceCard(SaveCardRequestDto saveCardRequestDto) {
		return MultiChoiceCard.builder()
			.cardId(Id.generateNextId())
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.images(makeImages(saveCardRequestDto.getImages()))
			.multiChoiceAnswer(saveCardRequestDto.getMultiChoiceAnswer())
			.build();
	}

	private static List<Image> makeImages(String images) {
		return Arrays.stream(images.split(SPLIT_WORD)).map(Image::new).collect(Collectors.toList());
	}
}
