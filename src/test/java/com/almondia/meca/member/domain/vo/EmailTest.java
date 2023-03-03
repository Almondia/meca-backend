package com.almondia.meca.member.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 1. toString 시 내부 객체의 정보를 보여준다.
 * 2. email 형식의 문자열만 허용한다.
 */
class EmailTest {

	@Test
	@DisplayName("toString과 내부 객체 정보 일치")
	void shouldReturnIntervalValueWhenToString() {
		Email email = new Email("hello@naver.com");
		assertThat(email.toString()).isEqualTo("hello@naver.com");
	}

	@ParameterizedTest
	@DisplayName("Email format 검증")
	@CsvSource({
		"hello@@naver.com", "h3@", "hello.com",
	})
	void shouldThrowIllegalArgumentExceptionWhenInvalidInput(String input) {
		assertThatThrownBy(() -> new Email(input)).isInstanceOf(IllegalArgumentException.class);
	}
}