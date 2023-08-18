package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultiChoiceQuestionTest {

	@Test
	@DisplayName("퀴즈 문제에 비우거나 공백만 입력해서는 안됩니다")
	void shouldNotBeBlankTest() {
		String value = "";
		assertThatThrownBy(() -> MultiChoiceQuestion.of(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("퀴즈 문제에 비우거나 공백만 입력해서는 안됩니다");
	}

	@Test
	@DisplayName("퀴즈 문제는 [로 시작해서 ]로 끝나야 합니다")
	void shouldStartEndSquareBracketTest() {
		String value = "<p> hello, world </p>, \"hello, world\"";
		assertThatThrownBy(() -> MultiChoiceQuestion.of(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("퀴즈 문제는 [로 시작해서 ]로 끝나야 합니다");
	}

	@Test
	@DisplayName("퀴즈 문제의 선택지는 5개 이하로 입력해야 합니다")
	void shouldNotMoreFiveSelectViewTest() {
		String value = "[\"<p>DDOS 공격이 아닌것은?</p>\",\"Trin00\",\"TFN\",\"TFN2k\",\"Stacheldraht\",\"TFN3k\",\"TFN4k\"]";
		assertThatThrownBy(() -> MultiChoiceQuestion.of(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("퀴즈 문제의 선택지는 1개 이상 5개 이하로 입력해야 합니다");
	}

	@Test
	@DisplayName("퀴즈 문제의 선택지는 1개 이상으로 입력해야 합니다")
	void shouldMoreOneSelectViewTest() {
		String value = "[\"<p>DDOS 공격이 아닌것은?</p>\"]";
		assertThatThrownBy(() -> MultiChoiceQuestion.of(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("퀴즈 문제의 선택지는 1개 이상 5개 이하로 입력해야 합니다");
	}

	@Test
	@DisplayName("퀴즈 문제의 선택지는 100자 이하로 입력해야 합니다")
	void shouldNotMore100QuizStringLengthTest() {
		String value = "[\"<p>DDOS 공격이 아닌것은?</p>\"]" + ",\"" + "a".repeat(101) + "\"" + "]";
		assertThatThrownBy(() -> MultiChoiceQuestion.of(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("퀴즈 문제의 선택지는 100자 이하로 입력해야 합니다");
	}

	@Test
	@DisplayName("51,000자를 초과해서 입력하면 안된다")
	void shouldNotMore51000Test() {
		String value = "a".repeat(51001);
		assertThatThrownBy(() -> MultiChoiceQuestion.of(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("51000를 초과해서 입력하셨습니다");
	}
}