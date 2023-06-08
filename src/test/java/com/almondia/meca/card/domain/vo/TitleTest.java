package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 1. 2이상 글자 입력 가능하다
 * 2. 40 이하의 글자를 입력 가능하다
 * 3. 공백만 입력할 수 없다
 */
class TitleTest {

	@ParameterizedTest
	@DisplayName("2이상 40 이하의 글자를 입력 가능하다")
	@CsvSource({
		"a"
	})
	void titleLengthTest(String input) {
		assertThatThrownBy(() -> new Title(input)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("40 이하의 글자를 입력 가능하다")
	void shouldInputUnder40Characters() {
		Title title = new Title("a".repeat(40));
		assertThat(title).isNotNull();
	}

	@Test
	@DisplayName("공백만 입력할 수 없다")
	void notBlankTest() {
		assertThatThrownBy(() -> new Title(" ".repeat(2))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("of 인스턴스 테스트")
	void ofInstanceTest() {
		Title title = Title.of("aa");
		assertThat(title).isNotNull();
	}
}