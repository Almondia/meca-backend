package com.almondia.meca.auth.oauth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.infra.CustomOAuth2Client;
import com.almondia.meca.auth.oauth.infra.dto.OAuth2AccessTokenResponse;

import reactor.core.publisher.Mono;

/**
 * 1. 외부 API 소통에 문제가 생기면 WebClient 관련 커스텀 Exception을 발생함
 */
class Oauth2ServiceTest {

	CustomOAuth2Client customOAuth2Client = Mockito.mock(CustomOAuth2Client.class);
	Oauth2Service oAuth2Service = new Oauth2Service(customOAuth2Client);

	@Test
	void throwExceptionTestWhenRequestAccessTokenThrow() {
		Mockito.doThrow(new BadWebClientRequestException("bad Request"))
			.when(customOAuth2Client)
			.requestAccessToken(eq("kakao"), anyString());
		assertThatThrownBy(() -> oAuth2Service.requestUserInfo("kakao", "asdf")).isInstanceOf(
			BadWebClientRequestException.class);
	}

	@Test
	void throwExceptionTestWhenRequestUserInfoTokenThrow() {
		Mockito.doReturn(Mono.just(OAuth2AccessTokenResponse.builder().build()))
			.when(customOAuth2Client)
			.requestAccessToken(eq("kakao"), anyString());
		Mockito.doThrow(new BadWebClientRequestException("bad Request"))
			.when(customOAuth2Client)
			.requestUserInfo(eq("kakao"), any());
		assertThatThrownBy(() -> oAuth2Service.requestUserInfo("kakao", "asdf")).isInstanceOf(
			BadWebClientRequestException.class);
	}
}