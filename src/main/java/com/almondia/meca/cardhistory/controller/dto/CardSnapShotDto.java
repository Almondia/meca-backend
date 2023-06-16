package com.almondia.meca.cardhistory.controller.dto;

import java.time.LocalDateTime;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
import com.almondia.meca.common.domain.vo.Id;

import lombok.Getter;

@Getter
public class CardSnapShotDto {
	private final Id cardId;
	private final Id memberId;
	private final Title title;
	private final Question question;
	private final String answer;
	private final CardType cardType;
	private final Description description;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;

	public CardSnapShotDto(Id cardId, CardSnapShot cardSnapShot) {
		this.cardId = cardId;
		this.memberId = cardSnapShot.getMemberId();
		this.title = cardSnapShot.getTitle();
		this.question = cardSnapShot.getQuestion();
		this.answer = cardSnapShot.getAnswer();
		this.cardType = cardSnapShot.getCardType();
		this.description = cardSnapShot.getDescription();
		this.createdAt = cardSnapShot.getCreatedAt();
		this.modifiedAt = cardSnapShot.getModifiedAt();
	}
}
