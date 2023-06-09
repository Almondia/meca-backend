package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 문제를 공배으로 비워 둘 수 없습니다
 * 500자 이상 입력할 수 없습니다
 * of 인스턴스 테스트
 */
class QuestionTest {

	@Test
	@DisplayName("문제를 공배으로 비워 둘 수 없습니다")
	void notEmptyQuestion() {
		String input = " ";
		assertThatThrownBy(() -> new Question(input)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("500자 이상 입력할 수 없습니다")
	void shouldThrowMoreThan500() {
		String input = "a".repeat(501);
		assertThatThrownBy(() -> new Question(input)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("of 인스턴스 테스트")
	void ofInstanceTest() {
		String input = "테스트";
		Question question = Question.of(input);
		assertThat(question).isInstanceOf(Question.class);
		assertThat(question.toString()).isEqualTo(input);
	}
}