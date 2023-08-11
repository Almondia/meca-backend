package com.almondia.meca.category.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class IsRecommendDto {
	private final boolean liked;
}
