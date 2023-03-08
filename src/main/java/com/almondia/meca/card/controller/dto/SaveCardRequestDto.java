package com.almondia.meca.card.controller.dto;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class SaveCardRequestDto {

	private final Title title;
	private final Question question;
	private final Id categoryId;
	private final String images;
	private final CardType cardType;
	private final OxAnswer oxAnswer;
	private final KeywordAnswer keywordAnswer;
	private final MultiChoiceAnswer multiChoiceAnswer;
}
