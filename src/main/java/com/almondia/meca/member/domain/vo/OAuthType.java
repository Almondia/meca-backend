package com.almondia.meca.member.domain.vo;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthType {

	KAKAO("kakao"),
	NAVER("naver"),
	GOOGLE("google");

	private static final String ERROR_MESSAGE = "잘못된 입력입니다. kakao, naver, google 셋 중 하나 입력해주세요";
	private final String details;

	public static OAuthType fromOAuthType(String value) {
		return Stream.of(OAuthType.values())
			.filter(oAuthType -> value.equals(oAuthType.details))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE));
	}
}
