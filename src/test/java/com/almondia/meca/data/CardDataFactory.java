package com.almondia.meca.data;

import java.util.ArrayList;
import java.util.List;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceQuestion;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.data.random.RandomAlphabetGenerator;
import com.almondia.meca.data.random.RandomStringGenerator;

import lombok.Getter;

@Getter
public class CardDataFactory implements TestDataFactory<Card> {

	private static final RandomStringGenerator generator = new RandomAlphabetGenerator();

	private final Id memberId = Id.generateNextId();
	private final Id categoryId = Id.generateNextId();

	@Override
	public List<Card> createTestData() {
		List<Card> cards = new ArrayList<>();

		for (int i = 0; i < 3; ++i) {
			cards.add(OxCard.builder()
				.cardId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.title(new Title(generator.generate(10)))
				.categoryId(categoryId)
				.question(new Question(generator.generate(30)))
				.memberId(memberId)
				.images(List.of(new Image("A"), new Image("B"), new Image("C")))
				.isDeleted(false)
				.oxAnswer(OxAnswer.O)
				.build());
		}
		cards.add(OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.title(new Title(generator.generate(10)))
			.categoryId(Id.generateNextId())
			.question(new Question(generator.generate(30)))
			.memberId(memberId)
			.images(List.of(new Image("A"), new Image("B"), new Image("C")))
			.isDeleted(false)
			.oxAnswer(OxAnswer.O)
			.build());
		for (int i = 0; i < 3; ++i) {
			cards.add(KeywordCard.builder()
				.cardId(Id.generateNextId())
				.cardType(CardType.KEYWORD)
				.title(new Title(generator.generate(10)))
				.categoryId(categoryId)
				.question(new Question(generator.generate(30)))
				.memberId(memberId)
				.images(List.of(new Image("A"), new Image("B"), new Image("C")))
				.isDeleted(false)
				.keywordAnswer(new KeywordAnswer("keyword"))
				.build());
		}
		for (int i = 0; i < 3; ++i) {
			cards.add(MultiChoiceCard.builder()
				.cardId(Id.generateNextId())
				.cardType(CardType.KEYWORD)
				.title(new Title(generator.generate(10)))
				.categoryId(categoryId)
				.question(MultiChoiceQuestion.of("[" + generator.generate(30) + ",\"1\"" + "]"))
				.memberId(memberId)
				.images(List.of(new Image("A"), new Image("B"), new Image("C")))
				.isDeleted(false)
				.multiChoiceAnswer(new MultiChoiceAnswer(1))
				.build());
		}
		cards.add(OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.title(new Title(generator.generate(10)))
			.categoryId(Id.generateNextId())
			.question(new Question(generator.generate(30)))
			.memberId(Id.generateNextId())
			.images(List.of(new Image("A"), new Image("B"), new Image("C")))
			.isDeleted(false)
			.oxAnswer(OxAnswer.O)
			.build());
		return cards;
	}
}
