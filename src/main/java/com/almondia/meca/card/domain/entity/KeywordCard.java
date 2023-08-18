package com.almondia.meca.card.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.Question;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("KEYWORD")
@AllArgsConstructor
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordCard extends Card {

	@Embedded
	@AttributeOverride(name = "question", column = @Column(name = "question", nullable = false, length = 5_1000))
	private Question question;

	@Embedded
	@AttributeOverride(name = "keywordAnswer", column = @Column(name = "keyword_answer"))
	private KeywordAnswer keywordAnswer;

	@Override
	public CardType getCardType() {
		return CardType.KEYWORD;
	}

	@Override
	public void changeQuestion(String question) {
		this.question = Question.of(question);
	}

	@Override
	public void changeAnswer(String answer) {
		this.keywordAnswer = KeywordAnswer.valueOf(answer);
	}

	@Override
	public String getQuestion() {
		return this.question.toString();
	}

	@Override
	public String getAnswer() {
		return this.keywordAnswer.toString();
	}
}
