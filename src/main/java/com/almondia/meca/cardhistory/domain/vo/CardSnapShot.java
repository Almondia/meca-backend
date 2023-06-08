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

	private Title title;
	private Question question;
	private String answer;
	private CardType cardType;
	private Description description;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	@Builder
	public CardSnapShot(
		Title title,
		Question question,
		String answer,
		CardType cardType,
		Description description,
		LocalDateTime createdAt,
		LocalDateTime modifiedAt
	) {
		Assert.noNullElements(
			new Object[] {title, question, answer, cardType, description, createdAt,
				modifiedAt},
			"CardSnapShot must not be null");
		this.title = title;
		this.question = question;
		this.answer = answer;
		this.cardType = cardType;
		this.description = description;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}
}
