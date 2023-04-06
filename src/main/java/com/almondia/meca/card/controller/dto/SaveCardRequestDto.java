package com.almondia.meca.card.controller.dto;

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.EditText;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SaveCardRequestDto {

	private Title title;
	private Question question;
	private Id categoryId;
	private CardType cardType;
	private String answer;
	private EditText editText;
}
