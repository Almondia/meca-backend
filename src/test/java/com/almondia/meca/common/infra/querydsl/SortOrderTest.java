package com.almondia.meca.common.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 1. fromString 정적 메서드의 asc 인스턴스 반환 테스트
 * 2. fromString 정적 메서드의 desc 인스턴스 반환 테스트
 */
class SortOrderTest {

	@ParameterizedTest
	@DisplayName("fromString 정적 메서드의 asc 인스턴스 반환 테스트")
	@CsvSource({
		"asc", "Asc", "aSc"
	})
	void shouldReturnSortOrderWhenInputAsc(String input) {
		SortOrder sortOrder = SortOrder.fromString(input);
		assertThat(sortOrder).isEqualTo(SortOrder.ASC);
	}

	@ParameterizedTest
	@DisplayName("fromString 정적 메서드의 desc 인스턴스 반환 테스트")
	@CsvSource({
		"desc", "Desc", "dEsc"
	})
	void shouldReturnSortOrderWhenInputDesc(String input) {
		SortOrder sortOrder = SortOrder.fromString(input);
		assertThat(sortOrder).isEqualTo(SortOrder.DESC);
	}
}