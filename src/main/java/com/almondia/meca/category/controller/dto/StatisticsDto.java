package com.almondia.meca.category.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StatisticsDto {
	private final double scoreAvg;
	private final long solveCount;
	private final long totalCount;
}
