package com.almondia.meca.card.domain.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.OxAnswer;

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

	@Enumerated(EnumType.STRING)
	private OxAnswer oxAnswer;

	@Override
	public CardType getCardType() {
		return CardType.OX_QUIZ;
	}
}
