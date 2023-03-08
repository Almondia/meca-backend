package com.almondia.meca.card.sevice.helper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. OxCard 속성별 인스턴스를 잘 생성했는지 검증
 * 2. KeywordCard 속성별 인스턴스를 잘 생성했는지 검증
 * 3. MultiChoiceCard 속성별 인스턴스 잘 생성했는지 검증
 */
class CardFactoryTest {

	@Test
	@DisplayName("OxCard 속성별 인스턴스를 잘 생성했는지 검증")
	public void newInstanceOxCardTest() {
		Card card = CardFactory.genCard(makeSaveCardRequest()
			.oxAnswer(OxAnswer.O)
			.cardType(CardType.OX_QUIZ)
			.build());
		assertThat(card).isInstanceOf(OxCard.class);
		assertThat(card).hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("oxAnswer");
	}

	@Test
	@DisplayName("KeywordCard 속성별 인스턴스를 잘 생성했는지 검증")
	public void newInstanceKeywordCardTest() {
		Card card = CardFactory.genCard(makeSaveCardRequest()
			.keywordAnswer(new KeywordAnswer("개발자"))
			.cardType(CardType.KEYWORD)
			.build());
		assertThat(card).isInstanceOf(KeywordCard.class);
		assertThat(card).hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("keywordAnswer");
	}

	@Test
	@DisplayName("MultiChoiceCard 속성별 인스턴스를 잘 생성했는지 검증")
	public void newInstanceMultiChoiceCardTest() {
		Card card = CardFactory.genCard(makeSaveCardRequest()
			.multiChoiceAnswer(new MultiChoiceAnswer(1))
			.cardType(CardType.MULTI_CHOICE)
			.build());
		assertThat(card).isInstanceOf(MultiChoiceCard.class);
		assertThat(card).hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("multiChoiceAnswer");
	}

	private SaveCardRequestDto.SaveCardRequestDtoBuilder makeSaveCardRequest() {
		return SaveCardRequestDto.builder()
			.title(new Title("title"))
			.question(new Question("question"))
			.categoryId(Id.generateNextId())
			.images("A,B,C");
	}
}