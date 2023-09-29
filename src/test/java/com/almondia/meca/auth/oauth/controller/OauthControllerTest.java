package com.almondia.meca.auth.oauth.controller;

import static com.almondia.meca.asciidocs.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.almondia.meca.auth.jwt.service.JwtTokenService;
import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.exception.BadWebClientResponseException;
import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.auth.oauth.service.Oauth2Service;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.SecurityConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.application.MemberService;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.repository.MemberRepository;
import com.almondia.meca.member.domain.vo.OAuthType;

/**
 * 1. 요청 성공시 AccessTokenResponseDto 속성에 담긴 값이 모두 출력되야 하며 CamelCase여야 함 성공 응답은 200
 * 2. oauth api 요청에 문제가 생긴 경우 401 응답
 * 3. oauth api 서버에 문제가 생겨 응답에 지장이 생긴 경우 500 응답
 * 4. 사용자 입력 오류시 400 반환
 */
@WebMvcTest({OauthController.class})
@ExtendWith({RestDocumentationExtension.class})
@Import({SecurityConfiguration.class, JacksonConfiguration.class})
class OauthControllerTest {

	@Autowired
	WebApplicationContext context;

	MockMvc mockMvc;

	@MockBean
	MemberService memberService;

	@MockBean
	MemberRepository memberRepository;

	@MockBean
	JwtTokenService jwtTokenService;

	@MockBean
	Oauth2Service oauth2Service;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.alwaysDo(print())
			.apply(documentationConfiguration(restDocumentation))
			.build();
	}

	@Test
	@DisplayName("요청 성공시 첫 로그인인 경우 AccessTokenResponseDto 속성에 담긴 값이 모두 출력되야 하며 camel case여야 함 성공 응답은 201")
	void shouldReturnAccessTokenResponseDtoAllPropertiesAndResponseStatusUsingCamelCaseTest() throws Exception {
		Mockito.doReturn(Member.builder().memberId(Id.generateNextId()).build()).when(memberService).save(any());
		Mockito.doReturn(OAuth2UserAttribute.of("id", "hello", "hello@naver.com", OAuthType.GOOGLE))
			.when(oauth2Service)
			.requestUserInfo(eq("kakao"), anyString());
		Mockito.doReturn("access token").when(jwtTokenService).createToken(any());

		mockMvc.perform(post("/api/v1/oauth/login/{registrationId}", "kakao")
				.param("code", "authorizeCode")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.accessToken").exists())
			.andDo(document("{class-name}/{method-name}",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(
					parameterWithName("registrationId").description("google, kakao, naver 중 하나의 값")
				),
				requestParameters(
					parameterWithName("code").description("oauth2 인증 후 발급받은 authorization code")
				),
				responseFields(
					fieldWithPath("accessToken").description("JWT Access Token")
				)
			));
	}

	@Test
	@DisplayName("한번 로그인한 적이 있는 경우 AccessTokenResponseDto를 출력하며 성공 응답은 200")
	void shouldReturnAccessTokenAndStatus200WhenDuplicateLogin() throws Exception {
		Mockito.doReturn(Optional.ofNullable(Member.builder().memberId(Id.generateNextId()).build()))
			.when(memberRepository)
			.findByOauthId(any());
		Mockito.doReturn(OAuth2UserAttribute.of("id", "hello", "hello@naver.com", OAuthType.GOOGLE))
			.when(oauth2Service)
			.requestUserInfo(eq("kakao"), anyString());
		Mockito.doReturn("access token").when(jwtTokenService).createToken(any());
		mockMvc.perform(post("/api/v1/oauth/login/{registrationId}", "kakao")
				.param("code", "authorizeCode")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists())
			.andDo(document("{class-name}/{method-name}",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(
					parameterWithName("registrationId").description("google, kakao, naver 중 하나의 값")
				),
				requestParameters(
					parameterWithName("code").description("oauth2 인증 후 발급받은 authorization code")
				),
				responseFields(
					fieldWithPath("accessToken").description("JWT Access Token")
				)
			));

	}

	@Test
	@DisplayName("oauth api 요청에 문제가 생긴 경우 401 응답")
	void shouldThrow400WhenExternalApiFailBecauseOfClientFault() throws Exception {
		Mockito.doThrow(new BadWebClientRequestException("bad request"))
			.when(oauth2Service).requestUserInfo(anyString(), anyString());

		mockMvc.perform(post("/api/v1/oauth/login/{registrationId}", "kakao")
				.param("code", "authorizeCode")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
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

	@Test
	@DisplayName("사용자 입력 오류시 400 반환")
	void shouldThrow400WhenUserBadRequestTest() throws Exception {
		Mockito.doThrow(new IllegalArgumentException("bad request"))
			.when(oauth2Service).requestUserInfo(anyString(), anyString());

		mockMvc.perform(post("/api/v1/oauth/login/{registrationId}", "kakao")
				.param("code", "authorizeCode")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("message").exists());
	}
}