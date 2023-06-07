package com.almondia.meca.cardhistory.domain.vo;

import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;

import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public final class CardSnapShot {

	private Title cardTitle;
	private Question cardQuestion;
	private String cardAnswer;
	private CardType cardType;
	private Description cardDescription;
	private LocalDateTime cardCreatedAt;
	private LocalDateTime cardModifiedAt;

	@Builder
	public CardSnapShot(
		Title cardTitle,
		Question cardQuestion,
		String cardAnswer,
		CardType cardType,
		Description cardDescription,
		LocalDateTime cardCreatedAt,
		LocalDateTime cardModifiedAt
	) {
		Assert.noNullElements(
			new Object[] {cardTitle, cardQuestion, cardAnswer, cardType, cardDescription, cardCreatedAt,
				cardModifiedAt},
			"CardSnapShot must not be null");
		this.cardTitle = cardTitle;
		this.cardQuestion = cardQuestion;
		this.cardAnswer = cardAnswer;
		this.cardType = cardType;
		this.cardDescription = cardDescription;
		this.cardCreatedAt = cardCreatedAt;
		this.cardModifiedAt = cardModifiedAt;
	}
}
