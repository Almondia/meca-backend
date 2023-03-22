package com.almondia.meca.member.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 1. toString 시 내부 객체의 정보를 보여준다.
 * 2. 영문자, 또는 한글 1 ~ 20까지 허용한다.
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

	@ParameterizedTest
	@CsvSource({
		"as1", "12", "aaasdfaasdsdsdsdsdsdaa", "가1",
	})
	void shouldThrowIllegalArgumentExceptionWhenInvalidInput(String input) {
		assertThatThrownBy(() -> new Name(input)).isInstanceOf(IllegalArgumentException.class);
	}
}