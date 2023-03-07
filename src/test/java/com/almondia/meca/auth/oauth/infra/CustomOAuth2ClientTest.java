package com.almondia.meca.auth.oauth.infra;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

import com.almondia.meca.auth.oauth.infra.dto.OAuth2AccessTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 1. 서버에 access token 요청시 성공하면 CustomTokenResponse 형태로 반환받을 수 있어야 함.
 * 2. 서버에 user info 요청시 성공하면 Map 형태로 정상적으로 반환하는지 검증.
 */
class CustomOAuth2ClientTest {

	static final String CLIENT_ID = "client";
	static final String CLIENT_SECRET = "secret";
	static final String REDIRECT_URI = "https://localhost:3000/callback";
	static final String SCOPE = "email";

	ObjectMapper objectMapper = new ObjectMapper();
	WebClient webClient;
	CustomOAuth2Client oauth2Client;
	MockWebServer mockWebServer;

	@BeforeEach
	void before() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.setDispatcher(makeDispatcher());
		mockWebServer.start();
		ClientRegistration kakao = makeClientRegistration();
		FakeClientRegistrationRepository repository = new FakeClientRegistrationRepository();
		repository.addRegistration(kakao);
		webClient = WebClient.builder().build();
		oauth2Client = new CustomOAuth2Client(repository, webClient);
	}

	@AfterEach
	void after() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	@DisplayName("서버에 token 요청 성공시 OAuth2TokenResponse.class형태로 응답이 잘 되는지 검증")
	void requestAccessTokenSuccessTest() {
		Mono<OAuth2AccessTokenResponse> response = oauth2Client.requestAccessToken("kakao",
			"authorization code");

		StepVerifier.create(response)
			.consumeNextWith(oAuth2AccessTokenResponse ->
				assertThat(oAuth2AccessTokenResponse).isNotNull())
			.verifyComplete();
	}

	@Test
	@DisplayName("서버에 userInfo 요청 성공시 Map<String, Object> 형태로 응답이 잘 되는지 검증")
	void requestUserInfoTokenSuccessTest() {
		Mono<Map<String, Object>> response = oauth2Client.requestUserInfo("kakao", makeTokenResponse());

		StepVerifier.create(response)
			.consumeNextWith(stringObjectMap ->
				assertThat(stringObjectMap).isNotNull())
			.verifyComplete();
	}

	private ClientRegistration makeClientRegistration() {
		return ClientRegistration.withRegistrationId("kakao")
			.clientId(CLIENT_ID)
			.clientSecret(CLIENT_SECRET)
			.clientName("kakao")
			.tokenUri(mockWebServer.url("/token").uri().toString())
			.userInfoUri(mockWebServer.url("/user").uri().toString())
			.scope(SCOPE)
			.redirectUri(REDIRECT_URI)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.authorizationUri(mockWebServer.url("/authorize").uri().toString())
			.build();
	}

	private OAuth2AccessTokenResponse makeTokenResponse() {
		return OAuth2AccessTokenResponse.builder()
			.accessToken("adfadfafasd")
			.refreshToken("asdadfa")
			.expiresIn(2014231L)
			.scope("email")
			.tokenType("authorization_code")
			.build();
	}

	private Dispatcher makeDispatcher() throws JsonProcessingException {
		String jsonResponse = objectMapper.writeValueAsString(makeTokenResponse());
		return new Dispatcher() {
			@NotNull
			@Override
			public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
				String path = recordedRequest.getPath();
				assert path != null;
				if (path.contains("token")) {
					return new MockResponse()
						.setResponseCode(HttpStatus.OK.value())
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(jsonResponse);
				}
				if (path.contains("user")) {
					return new MockResponse()
						.setResponseCode(HttpStatus.OK.value())
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody("{\"email\":\"hello@naver.com\"}");
				}
				return new MockResponse()
					.setResponseCode(HttpStatus.NOT_FOUND.value());
			}
		};
	}
}