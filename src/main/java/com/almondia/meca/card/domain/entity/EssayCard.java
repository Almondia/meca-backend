package com.almondia.meca.card.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.EssayAnswer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ESSAY")
@AllArgsConstructor
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EssayCard extends Card {

	@Embedded
	@AttributeOverride(name = "essayAnswer", column = @Column(name = "essay_answer", length = 2000))
	private EssayAnswer essayAnswer;

	@Override
	public CardType getCardType() {
		return CardType.ESSAY;
	}

	@Override
	public void changeAnswer(String answer) {
		this.essayAnswer = EssayAnswer.valueOf(answer);
	}

	@Override
	public String getAnswer() {
		return essayAnswer.toString();
	}
}
