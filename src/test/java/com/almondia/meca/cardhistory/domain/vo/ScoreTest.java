package com.almondia.meca.cardhistory.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. Score 객체는 음수로 초기화 할 수 없다
 * 2. Score 객체는 100 이하의 점수만 생성 가능하다
 */
class ScoreTest {

	@Test
	@DisplayName("Score 객체는 음수로 초기화 할 수 없다")
	void scoreInstanceMustNotBeNegativeTest() {
		assertThatThrownBy(() -> new Score(-1)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Score 객체는 100 이하의 점수만 생성 가능하다")
	void scoreInstanceMustLessOrEqual100Test() {
		assertThatThrownBy(() -> new Score(101)).isInstanceOf(IllegalArgumentException.class);
	}
}