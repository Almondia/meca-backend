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

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
				}
				return new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value());
			}
		};
		mockWebServer.setDispatcher(dispatcher);
	}
}