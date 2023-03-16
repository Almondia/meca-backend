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
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.common.domain.vo.Id;

public class CardFactory {

	private static final String SPLIT_WORD = ",";

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
		return OxCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.images(makeImages(saveCardRequestDto.getImages()))
			.oxAnswer(OxAnswer.valueOf(answer.toUpperCase()))
			.build();
	}

	private static KeywordCard genKeywordCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		return KeywordCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.images(makeImages(saveCardRequestDto.getImages()))
			.keywordAnswer(new KeywordAnswer(answer))
			.build();
	}

	private static MultiChoiceCard genMultiChoiceCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		String answer = saveCardRequestDto.getAnswer();
		return MultiChoiceCard.builder()
			.cardId(Id.generateNextId())
			.memberId(memberId)
			.question(saveCardRequestDto.getQuestion())
			.title(saveCardRequestDto.getTitle())
			.categoryId(saveCardRequestDto.getCategoryId())
			.cardType(saveCardRequestDto.getCardType())
			.images(makeImages(saveCardRequestDto.getImages()))
			.multiChoiceAnswer(new MultiChoiceAnswer(Integer.parseInt(answer)))
			.build();
	}

	private static List<Image> makeImages(String images) {
		return Arrays.stream(images.split(SPLIT_WORD)).map(Image::new).collect(Collectors.toList());
	}
}
