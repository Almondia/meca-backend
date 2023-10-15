package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. 2이상 글자 입력 가능하다
 * 2. 40 이하의 글자를 입력 가능하다
 * 3. 공백만 입력할 수 없다
 */
class TitleTest {

	private static final int TITLE_MAX_LENGTH = 40;
	private static final int TITLE_MIN_LENGTH = 2;

	@Test
	@DisplayName("한글자만 입력한 경우 예외 발생")
	void titleLengthTest() {
		String input = "a";
		assertThatThrownBy(() -> Title.of(input)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("MAX_LENGTH를 초과한 경우 예외 발생")
	void titleMaxLengthTest() {
		String input = "a".repeat(TITLE_MAX_LENGTH + 1);
		assertThatThrownBy(() -> Title.of(input)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("공백만 입력할 수 없다")
	void notBlankTest() {
		String blankWord = " ".repeat(2);
		assertThatThrownBy(() -> Title.of(blankWord)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("of 인스턴스 테스트")
	void ofInstanceTest() {
		Title title = Title.of("aa");
		assertThat(title).isNotNull();
	}
}