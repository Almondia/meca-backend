package com.almondia.meca.cardhistory.controller.dto;

import java.time.LocalDateTime;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.vo.Name;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class CardHistoryResponseDto {

	private Id cardHistoryId;
	private Id solvedUserId;
	private Name solvedUserName;
	private Answer userAnswer;
	private Score score;
	private Id categoryId;
	private Id cardId;
	private String title;
	private CardType cardType;
	private Question question;
	private String answer;
	private LocalDateTime createdAt;
}
