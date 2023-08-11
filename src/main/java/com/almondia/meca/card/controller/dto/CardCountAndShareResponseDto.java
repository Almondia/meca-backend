package com.almondia.meca.card.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardCountAndShareResponseDto {
	private final long count;
	private final boolean shared;
}
