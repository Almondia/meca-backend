package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. keyword는 500 길이 이상 입력할 수 없습니다
 */
class KeywordAnswerTest {

	@Test
	@DisplayName("keyword는 500 길이 이상 입력할 수 없습니다")
	void validationTest() {
		assertThatThrownBy(() -> new KeywordAnswer("a".repeat(501))).isInstanceOf(IllegalArgumentException.class);
	}
}