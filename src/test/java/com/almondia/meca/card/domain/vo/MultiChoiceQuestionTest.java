package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultiChoiceQuestionTest {
	private static final int MAX_CHOICE_COUNT = 5;
	private static final int MIN_CHOICE_COUNT = 2;
	private static final int MAX_LENGTH_PER_VIEW_QUESTION = 100;
	private static final int MAX_LENGTH = 51_000;

	@Test
	@DisplayName("Question이 5_1000자를 초과하는 경우 예외 발생")
	void shouldThrowExceptionWhenQuestionLengthIsOver5_1000Test() {
		// given
		String startPart = "[\"<p>";
		String endPart = "</p>\",\"1\",\"2\"]";
		int length = startPart.length() + endPart.length();
		String inputString = startPart + "a".repeat(MAX_LENGTH - length + 1) + endPart;

		// expect
		assertThatThrownBy(() -> MultiChoiceQuestion.of(inputString))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("최대 " + MAX_LENGTH + "자까지 입력 가능합니다");
	}

	@Test
	@DisplayName("제대로된 jsonArray 형식이 아닌 경우 예외 발생")
	void shouldThrowExceptionWhenInputIsNotJsonArrayTest() {
		// given
		String inputString = "not json array";

		// expect
		assertThatThrownBy(() -> MultiChoiceQuestion.of(inputString))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("JSON 형식이 아닙니다");
	}

	@Test
	@DisplayName("선택지가 2개 미만인 경우 예외 발생")
	void shouldThrowExceptionWhenChoiceCountIsLessThan2Test() {
		// given
		String inputString = "[\"<p>question</p>\",\"1\"]";

		// expect
		assertThatThrownBy(() -> MultiChoiceQuestion.of(inputString))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage(String.format("선택지는 최소 %d개 이상이어야 합니다", MIN_CHOICE_COUNT));
	}

	@Test
	@DisplayName("선택지가 5개 초과인 경우 예외 발생")
	void shouldThrowExceptionWhenChoiceCountIsMoreThan5Test() {
		// given
		String inputString = "[\"<p>question</p>\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\"]";

		// expect
		assertThatThrownBy(() -> MultiChoiceQuestion.of(inputString))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage(String.format("선택지는 최대 %d개 이하여야 합니다", MAX_CHOICE_COUNT));
	}

	@Test
	@DisplayName("보기 길이가 100자 초과인 경우 예외 발생")
	void shouldThrowExceptionWhenViewLengthIsMoreThan100Test() {
		// given
		String lastView = "a".repeat(MAX_LENGTH_PER_VIEW_QUESTION + 1);
		String inputString = String.format("[\"<p>question</p>\",\"1\",\"2\",\"%s\"]", lastView);

		// expect
		assertThatThrownBy(() -> MultiChoiceQuestion.of(inputString))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage(String.format("보기는 최대 %d자까지 입력 가능합니다", MAX_LENGTH_PER_VIEW_QUESTION));
	}

	@Test
	@DisplayName("보기가 비어있는 경우 예외 발생")
	void shouldThrowExceptionWhenViewIsBlankTest() {
		// given
		String inputString = "[\"<p>question</p>\",\"1\",\"2\",\"\",\"4\"]";

		// expect
		assertThatThrownBy(() -> MultiChoiceQuestion.of(inputString))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("보기는 비어 있을 수 없습니다");
	}

}