package com.almondia.meca.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.exception.BadWebClientResponseException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(BadWebClientRequestException.class)
	public ResponseEntity<ErrorResponseDto> handleBadWebClientRequestException(BadWebClientRequestException e) {
		log.error(e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponseDto.of(e));
	}

	@ExceptionHandler(BadWebClientResponseException.class)
	public ResponseEntity<ErrorResponseDto> handleBadWebClientResponseException(BadWebClientResponseException e) {
		log.error(e.getMessage());
		return ResponseEntity.internalServerError().body(ErrorResponseDto.of(e));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
		log.error(e.getMessage());
		return ResponseEntity.badRequest().body(ErrorResponseDto.of(e));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
		log.error(e.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponseDto.of(e));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> handleValueInstantiationException(HttpMessageNotReadableException e) {
		log.error(e.getMessage());
		if (e.getRootCause() != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.of(e.getRootCause()));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.ofErrorMessage("사용자 입력 오류"));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponseDto> handleMethodArgumentTYpeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.error(e.getMessage());
		if (e.getRootCause() != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.of(e.getRootCause()));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto.ofErrorMessage("사용자 입력 오류"));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e) {
		log.error(e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ErrorResponseDto.ofErrorMessage("내부 서버 오류"));
	}
}
