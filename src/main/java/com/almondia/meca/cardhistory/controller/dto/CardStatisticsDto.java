package com.almondia.meca.cardhistory.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CardStatisticsDto {

	private final Double scoreAvg;
	private final Long tryCount;
}
