package com.almondia.meca.cardhistory.infra.morpheme;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class EngNlpToken implements NlpToken {

	private final String morph;
	private final String pos;
}
