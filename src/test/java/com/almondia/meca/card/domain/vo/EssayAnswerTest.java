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

	@Test
	void shouldThrowExceptionWhenAnswerLengthIsOver500() {
		// given
		String answer = "anwer".repeat(101);

		// when
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> EssayAnswer.valueOf(answer));
	}
}