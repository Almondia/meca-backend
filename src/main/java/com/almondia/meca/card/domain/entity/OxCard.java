package com.almondia.meca.card.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("OX_QUIZ")
@AllArgsConstructor
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OxCard extends Card {

	@Embedded
	@AttributeOverride(name = "question", column = @Column(name = "question", nullable = false, length = 5_1000))
	private Question question;

	@Enumerated(EnumType.STRING)
	@Column(name = "ox_answer", length = 2)
	private OxAnswer oxAnswer;

	@Override
	public CardType getCardType() {
		return CardType.OX_QUIZ;
	}

	@Override
	public void changeQuestion(String question) {
		this.question = Question.of(question);
	}

	@Override
	public void changeAnswer(String answer) {
		this.oxAnswer = OxAnswer.valueOf(answer);
	}

	@Override
	public String getQuestion() {
		return this.question.toString();
	}

	@Override
	public String getAnswer() {
		return this.oxAnswer.toString();
	}

}
