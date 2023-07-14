package com.almondia.meca.cardhistory.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CardStatisticsDto {

	private final double scoreAvg;
	private final long solveCount;
}
