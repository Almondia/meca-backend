package com.almondia.meca.card.controller.dto;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveCardRequestDto {

	private Title title;
	private Question question;
	private Id categoryId;
	private String images;
	private CardType cardType;
	private OxAnswer oxAnswer;
	private KeywordAnswer keywordAnswer;
	private MultiChoiceAnswer multiChoiceAnswer;
}
