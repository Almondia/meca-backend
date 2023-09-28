package com.almondia.meca.common.error;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.exception.BadWebClientResponseException;

class GlobalControllerExceptionHandlerTest {

	private final GlobalControllerExceptionHandler globalControllerExceptionHandler = new GlobalControllerExceptionHandler();

	@Test
	@DisplayName("handleBadWebClientRequestException")
	void handleBadWebClientRequestExceptionTest() {
		BadWebClientRequestException exception = new BadWebClientRequestException("BadWebClientRequestException");
		ResponseEntity<ErrorResponseDto> responseEntity = globalControllerExceptionHandler.handleBadWebClientRequestException(
			exception);
		assertThat(responseEntity).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(responseEntity).extracting(ResponseEntity::getBody).isInstanceOf(ErrorResponseDto.class);
	}

	@Test
	@DisplayName("handleBadWebClientResponseException")
	void handleBadWebClientResponseExceptionTest() {
		BadWebClientResponseException exception = new BadWebClientResponseException("BadWebClientRequestException");
		ResponseEntity<ErrorResponseDto> responseEntity = globalControllerExceptionHandler.handleBadWebClientResponseException(
			exception);
		assertThat(responseEntity).extracting(ResponseEntity::getStatusCode)
			.isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(responseEntity).extracting(ResponseEntity::getBody).isInstanceOf(ErrorResponseDto.class);
	}

	@Test
	@DisplayName("handleIllegalArgumentException")
	void handleIllegalArgumentExceptionTest() {
		IllegalArgumentException exception = new IllegalArgumentException("IllegalArgumentException");
		ResponseEntity<ErrorResponseDto> responseEntity = globalControllerExceptionHandler.handleIllegalArgumentException(
			exception);
		assertThat(responseEntity).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity).extracting(ResponseEntity::getBody).isInstanceOf(ErrorResponseDto.class);
	}

	@Test
	@DisplayName("handleAccessDeniedException")
	void handleAccessDeniedExceptionTest() {
		AccessDeniedException exception = new AccessDeniedException("AccessDeniedException");
		ResponseEntity<ErrorResponseDto> responseEntity = globalControllerExceptionHandler.handleAccessDeniedException(
			exception);
		assertThat(responseEntity).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(responseEntity).extracting(ResponseEntity::getBody).isInstanceOf(ErrorResponseDto.class);
	}

	@Test
	@DisplayName("handleValueInstantiationException")
	void handleValueInstantiationExceptionTest() {
		HttpMessageNotReadableException exception = new HttpMessageNotReadableException("IllegalArgumentException");
		ResponseEntity<ErrorResponseDto> responseEntity = globalControllerExceptionHandler.handleValueInstantiationException(
			exception);
		assertThat(responseEntity).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity).extracting(ResponseEntity::getBody).isInstanceOf(ErrorResponseDto.class);
	}

	@Test
	@DisplayName("handleRuntimeException")
	void handleRuntimeExceptionTest() {
		RuntimeException exception = new RuntimeException("RuntimeException");
		ResponseEntity<ErrorResponseDto> responseEntity = globalControllerExceptionHandler.handleRuntimeException(
			exception);
		assertThat(responseEntity).extracting(ResponseEntity::getStatusCode)
			.isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(responseEntity).extracting(ResponseEntity::getBody).isInstanceOf(ErrorResponseDto.class);
	}

}