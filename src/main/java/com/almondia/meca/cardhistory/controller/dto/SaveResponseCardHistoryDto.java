package com.almondia.meca.cardhistory.controller.dto;

import com.almondia.meca.cardhistory.domain.vo.Score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveResponseCardHistoryDto {

	private final Score score;
}
