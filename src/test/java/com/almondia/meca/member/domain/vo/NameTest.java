package com.almondia.meca.member.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. toString 시 내부 객체의 정보를 보여준다.
 * 2. 문자열 길이는 1 ~ 20까지 허용한다
 * 3. 빈 공백을 입력으로 할 수 없다.
 */
class NameTest {

	@Test
	@DisplayName("빈 공백을 입력으로 할 수 없다")
	void shouldNotBlankTest() {
		assertThatThrownBy(() -> new Name("  ")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("toString과 내부 객체 정보 일치")
	void shouldReturnIntervalValueWhenToString() {
		Name name = new Name("최 번개");
		assertThat(name.toString()).isEqualTo("최 번개");
	}

	@Test
	@DisplayName("문자열 길이는 1 ~ 20까지 허용한다")
	void shouldThrowIllegalArgumentExceptionWhenInvalidInput() {
		assertThatThrownBy(() -> new Name("a".repeat(21))).isInstanceOf(IllegalArgumentException.class);
	}
}