package com.almondia.meca.common.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. 255길이 초과한 링크를 넣을 수 없습니다
 * 2. of 메소드 인스턴스 생성 테스트
 */
class ImageTest {

	@Test
	@DisplayName("255길이 초과한 링크를 넣을 수 없습니다")
	void validateLengthTest() {
		assertThatThrownBy(() -> new Image("a".repeat(256))).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("of 메소드 인스턴스 생성 테스트")
	void ofTest() {
		assertThat(Image.of("image")).isInstanceOf(Image.class);
	}
}