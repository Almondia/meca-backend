package com.almondia.meca.auth.s3.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PurposeTest {

	@ParameterizedTest
	@DisplayName("지정한 name 이외에 예외가 발생한다")
	@CsvSource({
		"p", "purp", "Thumbna"
	})
	void validationTest(String input) {
		assertThatThrownBy(() -> Purpose.ofDetails(input)).isInstanceOf(IllegalArgumentException.class);
	}
}