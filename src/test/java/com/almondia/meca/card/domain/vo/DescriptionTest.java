package com.almondia.meca.card.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * editText의 길이가 5_1000을 초과하면 IllegalArgumentException을 던진다
 * editText의 길이가 5_1000을 초과하지 않으면 정상적으로 데이터를 생성한다
 * equalsandhashcode 테스트
 * of 메서드 인스턴스 테스트
 */
class DescriptionTest {

	@Test
	void editTextLengthIsOver2_1000Test() {
		assertThrows(IllegalArgumentException.class, () -> new Description("a".repeat(5_1001)));
	}

	@Test
	void editTextLengthIsNotOver2_1000Test() {
		assertDoesNotThrow(() -> new Description("a".repeat(5_1000)));
	}

	@Test
	void equalsAndHashCodeTest() {
		Description description1 = new Description("a");
		Description description2 = new Description("a");
		assertEquals(description1, description2);
	}

	@Test
	@DisplayName("of 메서드 인스턴스 테스트")
	void ofInstanceTest() {
		Description description = Description.of("a");
		assertEquals(description, new Description("a"));
	}
}