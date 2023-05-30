package com.almondia.meca.cardhistory.controller.dto;

import java.time.LocalDateTime;

import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
@ToString
public class CardHistoryDto {

	private Id cardHistoryId;
	private Answer userAnswer;
	private Score score;
	private LocalDateTime createdAt;

	public CardHistoryDto(CardHistory cardHistory) {
		this.cardHistoryId = cardHistory.getCardHistoryId();
		this.userAnswer = cardHistory.getUserAnswer();
		this.score = cardHistory.getScore();
		this.createdAt = cardHistory.getCreatedAt();
	}

}
