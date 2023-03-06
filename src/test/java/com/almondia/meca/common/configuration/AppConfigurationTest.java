package com.almondia.meca.common.configuration;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.exception.BadWebClientResponseException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 1. 성공시 정상 응답 테스트
 * 2. timeout 4초 이상 걸릴 시 실패
 * 3. 4xx 응답인 경우 BadWebClientRequestException로 예외 핸들링
 * 4. 5xx 응답인 경우 BadWebClientResponseException로 예외 핸들링
 */
@ExtendWith(SpringExtension.class)
@Import(AppConfiguration.class)
class AppConfigurationTest {

	@Autowired
	WebClient webClient;

	static MockWebServer mockWebServer;

	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		setMockServer();
		mockWebServer.start();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void successTest() {
		Mono<String> response = webClient.get()
			.uri(mockWebServer.url("/v1/success").uri())
			.retrieve()
			.bodyToMono(String.class);

		StepVerifier.create(response)
			.expectNext("success")
			.verifyComplete();
	}

	@Test
	void timeoutTest() {
		Mono<String> response = webClient.get()
			.uri(mockWebServer.url("v1/timeout").uri())
			.retrieve()
			.bodyToMono(String.class);

		StepVerifier.create(response)
			.verifyError();
	}

	@Test
	void throwBadWebClientRequestExceptionWhenStatusCode4xx() {
		Mono<String> response = webClient.get()
			.uri(mockWebServer.url("v1/4xx").uri())
			.retrieve()
			.bodyToMono(String.class);

		StepVerifier.create(response)
			.expectErrorMatches(throwable -> throwable instanceof BadWebClientRequestException)
			.verify();
	}

	@Test
	void throwBadWebClientResponseExceptionWhenStatusCode4xx() {
		Mono<String> response = webClient.get()
			.uri(mockWebServer.url("v1/5xx").uri())
			.retrieve()
			.bodyToMono(String.class);

		StepVerifier.create(response)
			.expectErrorMatches(throwable -> throwable instanceof BadWebClientResponseException)
			.verify();
	}

	private static void setMockServer() {
		Dispatcher dispatcher = new Dispatcher() {
			@NotNull
			@Override
			public MockResponse dispatch(RecordedRequest request) {
				switch (Objects.requireNonNull(request.getPath())) {
					case "/v1/timeout":
						return new MockResponse()
							.setResponseCode(HttpStatus.OK.value())
							.setHeadersDelay(6L, TimeUnit.SECONDS);
					case "/v1/success":
						return new MockResponse()
							.setResponseCode(HttpStatus.OK.value())
							.setBody("success");
					case "/v1/4xx":
						return new MockResponse()
							.setResponseCode(HttpStatus.BAD_REQUEST.value())
							.setBody("bad request");
					case "/v1/5xx":
						return new MockResponse()
							.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
							.setBody("server error");
				}
				return new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value());
			}
		};
		mockWebServer.setDispatcher(dispatcher);
	}
}