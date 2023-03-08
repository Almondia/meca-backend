package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 1. 2이상 20 이하의 글자를 입력 가능하다
 * 2. 공백만 입력할 수 없다
 */
class TitleTest {

	@ParameterizedTest
	@DisplayName("2이상 20 이하의 글자를 입력 가능하다")
	@CsvSource({
		"a", "aaaaaaaaaaaaaaaaaaaaa"
	})
	void titleLengthTest(String input) {
		assertThatThrownBy(() -> new Title(input)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("공백만 입력할 수 없다")
	void test() {
		assertThatThrownBy(() -> new Title(" ".repeat(2))).isInstanceOf(IllegalArgumentException.class);
	}
}