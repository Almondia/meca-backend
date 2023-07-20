package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 */
class KeywordAnswerTest {

	@Test
	@DisplayName("keyword는 500 길이 이상 입력할 수 없습니다")
	void validationTest() {
		assertThatThrownBy(() -> new KeywordAnswer("a".repeat(501))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("키워드 응답 내에 containIgnoreCase 호출시 대소문자 구분 없이 키워드가 포함되어 있는지 확인한다")
	void containsIgnoreCaseTest() {
		KeywordAnswer keywordAnswer = new KeywordAnswer("test,power,energy");
		assertThat(keywordAnswer.containsIgnoreCase("TEST")).isTrue();
	}

	@Test
	@DisplayName("toString시 자기 자신의 타입 이름을 호출히야 한다")
	void toStringTest() {
		assertThat(KeywordAnswer.valueOf("test").toString()).isEqualTo("test");
	}

}