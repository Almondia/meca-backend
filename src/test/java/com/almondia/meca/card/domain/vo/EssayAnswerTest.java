package com.almondia.meca.card.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EssayAnswerTest {

	@Test
	void shouldMakeInstanceWhenCallOf() {
		// given
		String answer = "answer";

		// when
		EssayAnswer essayAnswer = EssayAnswer.valueOf(answer);

		// then
		assertEquals(answer, essayAnswer.toString());
	}
}