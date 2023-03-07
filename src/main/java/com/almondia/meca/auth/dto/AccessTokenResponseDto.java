package com.almondia.meca.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class AccessTokenResponseDto {
	private final String accessToken;

	public static AccessTokenResponseDto of(String accessToken) {
		return new AccessTokenResponseDto(accessToken);
	}
}
