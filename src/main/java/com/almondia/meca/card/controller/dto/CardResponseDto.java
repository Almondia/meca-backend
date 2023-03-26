package com.almondia.meca.card.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Image;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class CardResponseDto {

	private final Id cardId;
	private final Title title;
	private final Question question;
	private final List<Image> images;
	private final Id categoryId;
	private final CardType cardType;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
	private final String answer;
}
