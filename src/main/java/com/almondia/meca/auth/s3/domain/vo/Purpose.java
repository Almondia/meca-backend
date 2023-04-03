package com.almondia.meca.auth.s3.domain.vo;

import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum Purpose {

	THUMBNAIL("thumbnail"),
	PROFILE("profile"),
	CARD_IMAGE("card");

	private final String details;

	Purpose(String details) {
		this.details = details;
	}

	public static Purpose ofDetails(String details) {
		return Stream.of(values()).filter(purpose -> details.equals(purpose.details))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("thumbnail, profile, card 셋 중 하나만 입력해주세요"));
	}
}
