package com.almondia.meca.card.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * 1. editText의 길이가 2_1000을 초과하면 IllegalArgumentException을 던진다
 * 2. editText의 길이가 2_1000을 초과하지 않으면 정상적으로 데이터를 생성한다
 * 3. editText는 빈 공백을 허용하지 않는다
 * 4. equalsandhashcode 테스트
 */
class EditTextTest {

	@Test
	void editTextLengthIsOver2_1000Test() {
		assertThrows(IllegalArgumentException.class, () -> new EditText("a".repeat(2_1001)));
	}

	@Test
	void editTextLengthIsNotOver2_1000Test() {
		assertDoesNotThrow(() -> new EditText("a".repeat(2_1000)));
	}

	@Test
	void editTextIsBlankTest() {
		assertThrows(IllegalArgumentException.class, () -> new EditText(" "));
	}

	@Test
	void equalsAndHashCodeTest() {
		EditText editText1 = new EditText("a");
		EditText editText2 = new EditText("a");
		assertEquals(editText1, editText2);
	}
}