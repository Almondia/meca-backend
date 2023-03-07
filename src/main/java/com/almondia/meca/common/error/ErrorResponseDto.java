package com.almondia.meca.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponseDto {
	private String message;

	public static ErrorResponseDto of(Exception exception) {
		return new ErrorResponseDto(exception.getMessage());
	}
}
