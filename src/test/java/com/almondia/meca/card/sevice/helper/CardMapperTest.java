package com.almondia.meca.card.sevice.helper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Image;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 카드별 속성 변환이 잘 일어나는지 검증
 */
class CardMapperTest {

	@Test
	void shouldReturnCardResponseWhenCardTypeOxCardAndCardTest() {
		CardResponseDto dto = CardMapper.cardToDto(makeOxCard());
		assertThat(dto)
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("isDeleted")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt")
			.hasFieldOrProperty("answer");
	}

	@Test
	void mapperOxCardTest() {
		CardResponseDto dto = CardMapper.oxCardToDto(makeOxCard());
		assertThat(dto)
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("isDeleted")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt")
			.hasFieldOrProperty("answer");
	}

	@Test
	void mapperKeywordCardTest() {
		CardResponseDto dto = CardMapper.keywordCardToDto(makeKeywordCard());
		assertThat(dto)
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("isDeleted")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt")
			.hasFieldOrProperty("answer");
	}

	@Test
	void mapperMultiChoiceCardTest() {
		CardResponseDto dto = CardMapper.multiChoiceCardToDto(multiChoiceCard());
		assertThat(dto)
			.hasFieldOrProperty("cardId")
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("question")
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("images")
			.hasFieldOrProperty("cardType")
			.hasFieldOrProperty("isDeleted")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt")
			.hasFieldOrProperty("answer");
	}

	private OxCard makeOxCard() {
		return OxCard.builder()
			.cardId(Id.generateNextId())
			.title(new Title("title"))
			.question(new Question("question"))
			.images(List.of(new Image("A"), new Image("B"), new Image("C")))
			.categoryId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.oxAnswer(OxAnswer.O)
			.build();
	}

	private KeywordCard makeKeywordCard() {
		return KeywordCard.builder()
			.cardId(Id.generateNextId())
			.title(new Title("title"))
			.question(new Question("question"))
			.images(List.of(new Image("A"), new Image("B"), new Image("C")))
			.categoryId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.keywordAnswer(new KeywordAnswer("keyword"))
			.build();
	}

	private MultiChoiceCard multiChoiceCard() {
		return MultiChoiceCard.builder()
			.cardId(Id.generateNextId())
			.title(new Title("title"))
			.question(new Question("question"))
			.images(List.of(new Image("A"), new Image("B"), new Image("C")))
			.categoryId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.multiChoiceAnswer(new MultiChoiceAnswer(1))
			.build();
	}
}