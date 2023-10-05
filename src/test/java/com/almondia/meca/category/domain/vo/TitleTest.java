package com.almondia.meca.category.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. title 문자 길이는 40을 초과할 수 없다
 * 2. 빈 공백만으로 title을 초기화 할 수 없다
 * 3. 문자열을 기준으로 크기 비교가 가능하다
 * 4. of 메서드를 통해 초기화 할 수 있다
 * 5. valueOf 메서드를 통해 초기화 할 수 있다
 */
class TitleTest {

	@Test
	@DisplayName("title 문자 길이는 40을 초과할 수 없다")
	void shouldThrowWhenTitleLengthMoreThan20Test() {
		String more41 = "a".repeat(41);
		assertThatThrownBy(() -> Title.of(more41)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("빈 공백만으로 title을 초기화 할 수 없다")
	void shouldThrowWhenTitleIsBlankTest() {
		assertThatThrownBy(() -> new Title(" ")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("문자열을 기준으로 크기 비교가 가능하다")
	void shouldCompareToTitleByStringTest() {
		Title title1 = new Title("title1");
		Title title2 = new Title("title2");
		assertThat(title1).isLessThanOrEqualTo(title2);
	}

	@Test
	@DisplayName("of 메서드를 통해 초기화 할 수 있다")
	void shouldInitializeByOfMethodTest() {
		Title title = Title.of("title");
		assertThat(title).isNotNull();
	}

	@Test
	@DisplayName("valueOf 메서드를 통해 초기화 할 수 있다")
	void shouldInitializeByValueOfMethodTest() {
		Title title = Title.valueOf("title");
		assertThat(title).isNotNull();
	}

	@Test
	@DisplayName("toString 메서드를 통해 문자열로 변환할 수 있다")
	void shouldConvertToStringTest() {
		Title title = Title.valueOf("title");
		assertThat(title).extracting(Title::toString).isEqualTo("title");
	}
}