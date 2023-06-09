package com.almondia.meca.cardhistory.domain.vo;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.almondia.meca.card.domain.entity.Card;
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

	@AttributeOverride(name = "title", column = @Column(name = "card_title", nullable = false, length = 120))
	private Title title;

	@AttributeOverride(name = "question", column = @Column(name = "card_question", nullable = false, length = 500))
	private Question question;

	@AttributeOverride(name = "answer", column = @Column(name = "card_answer", nullable = false))
	private String answer;

	@Enumerated(EnumType.STRING)
	private CardType cardType;

	@AttributeOverride(name = "description", column = @Column(name = "card_description", nullable = false, length = 2_1000, columnDefinition = "TEXT"))
	private Description description;

	@Column(name = "card_created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "card_modified_at", nullable = false)
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

	public static CardSnapShot copyShot(Card card) {
		return CardSnapShot.builder()
			.title(card.getTitle())
			.question(card.getQuestion())
			.answer(card.getAnswer())
			.cardType(card.getCardType())
			.description(card.getDescription())
			.createdAt(card.getCreatedAt())
			.modifiedAt(card.getModifiedAt())
			.build();
	}
}
