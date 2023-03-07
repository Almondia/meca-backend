package com.almondia.meca.auth;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.almondia.meca.auth.jwt.service.JwtTokenService;
import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.exception.BadWebClientResponseException;
import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.auth.oauth.service.Oauth2Service;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.SecurityConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.service.MemberService;

/**
 *  1. 요청 성공시 AccessTokenResponseDto 속성에 담긴 값이 모두 출력되야 하며 snakeCase여야 함 성공 응답은 200
 *  2. oauth api 요청에 문제가 생긴 경우 400 응답
 *  3. oauth api 서버에 문제가 생겨 응답에 지장이 생긴 경우 500 응답
 */
@WebMvcTest({AuthController.class})
@Import({SecurityConfiguration.class, JacksonConfiguration.class})
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	MemberService memberService;

	@MockBean
	JwtTokenService jwtTokenService;

	@MockBean
	Oauth2Service oauth2Service;

	@Test
	@DisplayName("요청 성공시 AccessTokenResponseDto 속성에 담긴 값이 모두 출력되야 하며 snakeCase여야 함 성공 응답은 200")
	void shouldReturnAccessTokenResponseDtoAllPropertiesAndResponseStatusUsingSnakeCaseTest() throws Exception {
		Mockito.doReturn(Member.builder().memberId(Id.generateNextId()).build()).when(memberService).save(any());
		Mockito.doReturn(OAuth2UserAttribute.of("hello", "hello@naver.com", OAuthType.GOOGLE))
			.when(oauth2Service)
			.requestUserInfo(eq("kakao"), anyString());
		Mockito.doReturn("access token").when(jwtTokenService).createToken(any());

		mockMvc.perform(post("/api/v1/oauth/login/{registrationId}", "kakao")
				.param("code", "authorizeCode")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.access_token").exists());
	}

	@Test
	@DisplayName("oauth api 요청에 문제가 생긴 경우 400 응답")
	void shouldThrow400WhenExternalApiFailBecauseOfClientFault() throws Exception {
		Mockito.doThrow(new BadWebClientRequestException("bad request"))
			.when(oauth2Service).requestUserInfo(anyString(), anyString());

		mockMvc.perform(post("/api/v1/oauth/login/{registrationId}", "kakao")
				.param("code", "authorizeCode")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("message").exists());
	}

	@Test
	@DisplayName("oauth api 서버에 문제가 생겨 응답에 지장이 생긴 경우 500 응답")
	void shouldThrow500WhenExternalApiFailBecauseOfClientFault() throws Exception {
		Mockito.doThrow(new BadWebClientResponseException("bad response"))
			.when(oauth2Service).requestUserInfo(anyString(), anyString());

		mockMvc.perform(post("/api/v1/oauth/login/{registrationId}", "kakao")
				.param("code", "authorizeCode")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("message").exists());
	}
}