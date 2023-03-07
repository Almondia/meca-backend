package com.almondia.meca.common.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.exception.BadWebClientResponseException;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(BadWebClientRequestException.class)
	public ResponseEntity<ErrorResponseDto> handleBadWebClientRequestException(BadWebClientRequestException e) {
		return ResponseEntity.badRequest().body(ErrorResponseDto.of(e));
	}

	@ExceptionHandler(BadWebClientResponseException.class)
	public ResponseEntity<ErrorResponseDto> handleBadWebClientResponseException(BadWebClientResponseException e) {
		return ResponseEntity.internalServerError().body(ErrorResponseDto.of(e));
	}
}
