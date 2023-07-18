package com.almondia.meca.card.controller.dto;

import com.almondia.meca.cardhistory.controller.dto.CardStatisticsDto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CardWithStatisticsDto {
	private final CardDto card;
	private final CardStatisticsDto statistics;

}
