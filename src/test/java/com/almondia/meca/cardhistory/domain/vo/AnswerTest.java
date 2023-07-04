package com.almondia.meca.cardhistory.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. 길이가 20 초과시 예외 발생
 * 2. 공백을 입력으로 받을 시 예외
 */
class AnswerTest {

	@Test
	@DisplayName("길이가 500 초과시 예외 발생")
	void shouldThrowWhenLengthMoreThan500Test() {
		assertThatThrownBy(() -> new Answer("a".repeat(501))).isInstanceOf(IllegalArgumentException.class);
	}

}