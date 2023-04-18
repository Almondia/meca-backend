package com.almondia.meca.card.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * 1. editText의 길이가 2_1000을 초과하면 IllegalArgumentException을 던진다
 * 2. editText의 길이가 2_1000을 초과하지 않으면 정상적으로 데이터를 생성한다
 * 3. equalsandhashcode 테스트
 */
class DescriptionTest {

	@Test
	void editTextLengthIsOver2_1000Test() {
		assertThrows(IllegalArgumentException.class, () -> new Description("a".repeat(2_1001)));
	}

	@Test
	void editTextLengthIsNotOver2_1000Test() {
		assertDoesNotThrow(() -> new Description("a".repeat(2_1000)));
	}

	@Test
	void equalsAndHashCodeTest() {
		Description description1 = new Description("a");
		Description description2 = new Description("a");
		assertEquals(description1, description2);
	}
}