package com.almondia.meca.card.controller.dto;

import org.springframework.lang.Nullable;

import com.almondia.meca.card.domain.vo.Description;
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
public class UpdateCardRequestDto {

	@Nullable
	private Title title;

	@Nullable
	private String question;

	@Nullable
	private Id categoryId;

	@Nullable
	private Description description;

	@Nullable
	private String answer;
}
