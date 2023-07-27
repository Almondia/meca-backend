package com.almondia.meca.card.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CardCountGroupByScoreDto {
	private final double score;
	private final long count;
}
