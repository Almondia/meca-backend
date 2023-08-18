package com.almondia.meca.card.application.helper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.EssayCard;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. OxCard 속성별 인스턴스를 잘 생성했는지 검증
 * 2. KeywordCard 속성별 인스턴스를 잘 생성했는지 검증
 * 3. MultiChoiceCard 속성별 인스턴스 잘 생성했는지 검증
 * 4. EditText가 null이더라도, 인스턴스 생성을 할 수 있어야 한다
 * 5. EssayCard 인스턴스 생성 검증 테스트
 */
class CardFactoryTest {

	@Test
	@DisplayName("OxCard 속성별 인스턴스를 잘 생성했는지 검증")
	void newInstanceOxCardTest() {
		Card card = CardFactory.genCard(makeSaveCardRequest()
			.answer(OxAnswer.O.toString())
			.cardType(CardType.OX_QUIZ)
			.build(), Id.generateNextId());
		assertThat(card).isInstanceOf(OxCard.class)
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("memberId")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("oxAnswer");
	}

	@Test
	@DisplayName("KeywordCard 속성별 인스턴스를 잘 생성했는지 검증")
	void newInstanceKeywordCardTest() {
		Card card = CardFactory.genCard(makeSaveCardRequest()
			.answer("개발자")
			.cardType(CardType.KEYWORD)
			.build(), Id.generateNextId());
		assertThat(card).isInstanceOf(KeywordCard.class)
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("memberId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("keywordAnswer");
	}

	@Test
	@DisplayName("MultiChoiceCard 속성별 인스턴스를 잘 생성했는지 검증")
	void newInstanceMultiChoiceCardTest() {
		Card card = CardFactory.genCard(makeSaveCardRequest()
			.answer("1")
			.cardType(CardType.MULTI_CHOICE)
			.build(), Id.generateNextId());
		assertThat(card).isInstanceOf(MultiChoiceCard.class)
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("memberId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("multiChoiceAnswer");
	}

	private SaveCardRequestDto.SaveCardRequestDtoBuilder makeSaveCardRequest() {
		return SaveCardRequestDto.builder()
			.title(new Title("title"))
			.question("[\"<p>question</p>\",\"2\",\"3\",\"4\"]")
			.categoryId(Id.generateNextId())
			.description(new Description("editText"));
	}

	@Test
	@DisplayName("EditText가 null이더라도, 인스턴스 생성을 할 수 있어야 한다")
	void shouldCreateInstanceWithoutEditText() {
		Card card = CardFactory.genCard(makeSaveCardRequestWithoutEditText()
			.cardType(CardType.OX_QUIZ)
			.answer(OxAnswer.O.toString())
			.build(), Id.generateNextId());
		assertThat(card).isInstanceOf(OxCard.class)
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("memberId")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("oxAnswer");
	}

	@Test
	@DisplayName("EssayCard 인스턴스 생성 검증 테스트")
	void shouldCreateInstanceEssayCard() {
		Card card = CardFactory.genCard(makeSaveCardRequest()
			.cardType(CardType.ESSAY)
			.answer("essayAnswer")
			.build(), Id.generateNextId());
		assertThat(card).isInstanceOf(EssayCard.class)
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("memberId")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("cardId");
	}

	private SaveCardRequestDto.SaveCardRequestDtoBuilder makeSaveCardRequestWithoutEditText() {
		return SaveCardRequestDto.builder()
			.title(new Title("title"))
			.question("question")
			.categoryId(Id.generateNextId());
	}
}