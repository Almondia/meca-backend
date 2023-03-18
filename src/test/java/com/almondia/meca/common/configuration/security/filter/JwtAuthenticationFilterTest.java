package com.almondia.meca.common.configuration.security.filter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.almondia.meca.auth.jwt.service.JwtTokenService;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.member.service.MemberService;

/**
 * 1. 해당 필터는 access token이 입력되지 않았다면 그냥 통과한다
 * 2. access token이 존재할 떄 옳바르지 않다면 response에 에러를 넣고 401상태를 응답하며 더 이상 필터를 통과시키지 않는다
 * 3. access token이 정상적이라면 securityContextHolder에 authentication을 등록하고 필터를 통과시킨다
 * 4. 주입한 tokenService에서 예외 발생시 예외를 처리해야 한다
 * 5. 주입한 memberService에서 예외 발생시 예외를 처리해야 한다
 */
class JwtAuthenticationFilterTest {

	static JwtAuthenticationFilter jwtAuthenticationFilter;
	static JwtTokenService jwtTokenService;
	static MemberService memberService;
	HttpServletRequest request;
	HttpServletResponse response;
	FilterChain filterChain;

	@BeforeAll
	static void setUp() {
		jwtTokenService = Mockito.mock(JwtTokenService.class);
		memberService = Mockito.mock(MemberService.class);
		jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenService, memberService);
		Mockito.doReturn(Member.builder()
			.memberId(Id.generateNextId())
			.email(new Email("emial@naver.com"))
			.name(new Name("name"))
			.oAuthType(OAuthType.KAKAO)
			.role(Role.USER)
			.build()).when(memberService).findMember(any());
	}

	@BeforeEach
	void initialize() {
		request = Mockito.mock(HttpServletRequest.class);
		response = Mockito.spy(FakeHttpServletResponse.class);
		filterChain = Mockito.mock(FilterChain.class);
	}

	@Test
	@DisplayName("해당 필터는 access token이 입력되지 않았다면 그냥 통과한다")
	void shouldPassWhenNotInputAccessTokenTest() throws ServletException, IOException {
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		Mockito.verify(filterChain, Mockito.atLeastOnce()).doFilter(request, response);
	}

	@Test
	@DisplayName("access token이 존재할 떄 옳바르지 않다면 response에 에러를 넣고 401상태를 응답하며 더 이상 필터를 통과시키지 않는다")
	void shouldBlockAndErrorResponseWhenNotInputAccessTokenTest() throws ServletException, IOException {
		String invalidAccessToken = "asa";
		Mockito.doReturn("Bearer " + invalidAccessToken).when(request).getHeader("Authorization");

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
	}

	@Test
	@DisplayName("access token이 정상적이라면 securityContextHolder에 authentication을 등록하고 필터를 통과시킨다")
	void shouldPassBlockAndAddAuthenticationIntoSecurityContextWhenAccessTokenIsValidTest() throws
		ServletException,
		IOException {

		String validToken = "asda1231aad";
		Mockito.doReturn("Bearer " + validToken).when(request).getHeader("Authorization");
		Mockito.doReturn(true).when(jwtTokenService).isValidToken(validToken);
		Mockito.doReturn(Id.generateNextId().toString()).when(jwtTokenService).getIdFromToken(validToken);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain, Mockito.atLeastOnce()).doFilter(request, response);
	}

	@Test
	@DisplayName("주입한 tokenService에서 예외 발생시 예외를 처리해야 한다")
	void shouldHandleExceptionWhenThrowByTokenServiceTest() throws ServletException, IOException {
		String validToken = "asda1231aad";
		Mockito.doReturn("Bearer " + validToken).when(request).getHeader("Authorization");
		Mockito.doThrow(IllegalArgumentException.class).when(jwtTokenService).isValidToken(any());

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	@DisplayName("주입한 memberService에서 예외 발생시 예외를 처리해야 한다")
	void shouldHandleExceptionWhenThrowByMemberServiceTest() throws ServletException, IOException {
		String validToken = "asda1231aad";
		Mockito.doReturn("Bearer " + validToken).when(request).getHeader("Authorization");
		Mockito.doReturn(true).when(jwtTokenService).isValidToken(any());
		Mockito.doReturn(Id.generateNextId().toString()).when(jwtTokenService).getIdFromToken(anyString());
		Mockito.doThrow(IllegalArgumentException.class).when(memberService).findMember(any());

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}
}