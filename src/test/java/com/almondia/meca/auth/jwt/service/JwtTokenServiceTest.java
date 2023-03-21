package com.almondia.meca.auth.jwt.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.common.configuration.jwt.JwtProperties;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. jjwt 라이브러리를 활용한 토큰 생성 및 검증 테스트
 * 2. 토큰 검증 메서드는 예외를 출력하면 안된다.
 * 3. token을 통해 성공적으로 ID 문자열 값을 가져와야 한다
 * 4. jwt 규격보다 적거나 많은 토큰 입력시 isValid에서 캐치해서 false를 반환해야 한다
 */
class JwtTokenServiceTest {

	JwtTokenService tokenService;

	@BeforeEach
	void before() {
		JwtProperties fakeJwtProperties = new JwtProperties();
		fakeJwtProperties.setSecretKey(
			"asdfasdafd13123132asasdfasdfasdfasdfasdfAdqweqasdfasdafd13123132asasdfasdfasdfasdfasdfAdqweqasdfasdafd13123132asasdfasdfasdfasdfasdfAdqweq");
		fakeJwtProperties.setExpirationMs(86400000);
		tokenService = new JwtTokenService(fakeJwtProperties);
	}

	@Test
	@DisplayName("생선된 토큰이 유효한 토큰인지 검증")
	void makeValidTokenTest() {
		Id id = Id.generateNextId();
		String token = tokenService.createToken(id);
		boolean isValid = tokenService.isValidToken(token);
		assertThat(isValid).isTrue();
	}

	@Test
	@DisplayName("토큰 검증은 예외가 아닌 불리언 값을 리턴해야 한다")
	void shouldReturnBooleanWhenCheckValidTokenTest() {
		boolean isValid = tokenService.isValidToken("asd123");
		assertThat(isValid).isFalse();
	}

	@Test
	@DisplayName("생성한 jwt token을 추출했을 때 결과는 같아야 한다")
	void shouldSameIdFromAccessTokenTest() {
		Id id = Id.generateNextId();
		String token = tokenService.createToken(id);
		String id2 = tokenService.getIdFromToken(token);
		assertThat(id2).isEqualTo(id.toString());
	}

	@Test
	@DisplayName("jwt 규격보다 적거나 많은 토큰 입력시 isValid에서 캐치해서 false를 반환해야 한다")
	void shouldCatchSignatureExceptionWhenWrongFormatTest() {
		String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IjAxODZmOWY0LWZiMWItMjUzZS05NDZmLWFlNTIwMzYxZDIzMCIsImV4cCI6MTY3OTMxNzIyN30.CVGh73wqCer2RoTQlpUCYS4URzmKpM7bF3y-jc261hh-5tmjOU76LThRWJ80_JYiFNHPhSC8UiEoxg0Ma8j5";
		boolean validToken = tokenService.isValidToken(accessToken);
		assertThat(validToken).isFalse();
	}

}