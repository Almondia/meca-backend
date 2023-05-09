package com.almondia.meca.card.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("MULTI_CHOICE")
@AllArgsConstructor
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MultiChoiceCard extends Card {

	@Embedded
	@AttributeOverride(name = "number", column = @Column(name = "multi_choice_answer", length = 2))
	private MultiChoiceAnswer multiChoiceAnswer;

	@Override
	public CardType getCardType() {
		return CardType.MULTI_CHOICE;
	}
}
