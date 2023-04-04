package com.almondia.meca.auth.s3.domain.vo;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 1. fromString의 예외 테스트
 * 2. fromString 정상 테스트
 * 3. fromString의 대소문자 테스트
 * 4. fromString의 공백 테스트
 * 5. fromString의 null 테스트
 */
class ImageExtensionTest {

	@Test
	@DisplayName("fromString의 예외 테스트")
	void fromStringExceptionTest() {
		assertThrows(IllegalArgumentException.class, () -> ImageExtension.fromString("pnga"));
	}

	@ParameterizedTest
	@DisplayName("fromString 정상 테스트")
	@CsvSource({
		"png",
		"jpeg",
		"jpg",
		"gif"
	})
	void fromStringTest(String input) {
		assertThat(ImageExtension.fromString(input)).isNotNull();
	}

	@Test
	@DisplayName("fromString의 대소문자 테스트")
	void fromStringCaseTest() {
		assertEquals(ImageExtension.PNG, ImageExtension.fromString("PNG"));
	}

	@Test
	@DisplayName("fromString의 공백 테스트")
	void fromStringSpaceTest() {
		assertThrows(IllegalArgumentException.class, () -> ImageExtension.fromString(" "));
	}

	@Test
	@DisplayName("fromString의 null 테스트")
	void fromStringNullTest() {
		assertThrows(IllegalArgumentException.class, () -> ImageExtension.fromString(null));
	}
}