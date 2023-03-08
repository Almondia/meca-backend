package com.almondia.meca.card.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 1. 객관식 정답은 1~5의 숫자 이외의 입력을 받을 수 없습니다
 *
 */
class MultiChoiceAnswerTest {

	@ParameterizedTest
	@DisplayName("객관식 정답은 1~5의 숫자 이외의 입력을 받을 수 없습니다")
	@CsvSource({
		"0", "6"
	})
	void validateTest(int number) {
		assertThatThrownBy(() -> new MultiChoiceAnswer(number)).isInstanceOf(IllegalArgumentException.class);
	}
}