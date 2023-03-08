package com.almondia.meca.category.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. title 문자 길이는 20을 초과할 수 없다
 * 2. 빈 공백만으로 title을 초기화 할 수 없다
 */
class TitleTest {

	@Test
	@DisplayName("title 문자 길이는 20을 초과할 수 없다")
	void shouldThrowWhenTitleLengthMoreThan20Test() {
		assertThatThrownBy(() -> new Title("a".repeat(21))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("빈 공백만으로 title을 초기화 할 수 없다")
	void shouldThrowWhenTitleIsBlankTest() {
		assertThatThrownBy(() -> new Title(" ")).isInstanceOf(IllegalArgumentException.class);
	}

}