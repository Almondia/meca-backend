package com.almondia.meca.card.domain.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;

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
	private KeywordAnswer keywordAnswer;

	@Override
	public CardType getCardType() {
		return CardType.KEYWORD;
	}

	@Override
	public void changeAnswer(String answer) {
		this.keywordAnswer = KeywordAnswer.valueOf(answer);
	}

	@Override
	public String getAnswer() {
		return this.keywordAnswer.toString();
	}
}
