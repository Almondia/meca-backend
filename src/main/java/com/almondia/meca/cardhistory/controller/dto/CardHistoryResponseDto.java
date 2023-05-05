package com.almondia.meca.cardhistory.controller.dto;

import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.vo.Name;

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
public class CardHistoryResponseDto {

	private Id cardHistoryId;
	private Id memberId;
	private Name name;
	private Answer userAnswer;
	private Score score;
	private Id categoryId;
	private com.almondia.meca.category.domain.vo.Title categoryTitle;
	private Id cardId;
	private com.almondia.meca.card.domain.vo.Title cardTitle;
}
