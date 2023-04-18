package com.almondia.meca.card.infra.querydsl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CardSearchOption {
	private final String containTitle;
}
