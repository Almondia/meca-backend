package com.almondia.meca.cardhistory.infra.morpheme;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Morphemes<T> {

	private List<T> cardAnswerMorpheme;
	private List<T> userAnswerMorpheme;

}
