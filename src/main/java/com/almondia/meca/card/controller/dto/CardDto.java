package com.almondia.meca.card.controller.dto;

import java.time.LocalDateTime;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class CardDto {

	private final Id cardId;
	private final Title title;
	private final Id memberId;
	private final String question;
	private final Id categoryId;
	private final CardType cardType;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
	private final String answer;
	private final Description description;
}
