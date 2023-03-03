package com.almondia.meca.member.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.almondia.meca.common.domain.vo.Id;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * OAuthType은 kakao, google, naver 문자열 입력만 변환 가능
 */
@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OAuthTypeTest {

	@PersistenceContext
	private EntityManager entityManager;

	@ParameterizedTest
	@DisplayName("OAuthType에 정의된 인스턴스 멤버 변수만 타입으로 변환 가능")
	@CsvSource({"kakaos", "google3", "navera"})
	void shouldReturnOAuthTypeFromString(String registrationId) {
		assertThatThrownBy(() -> OAuthType.fromOAuthType(registrationId)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void converterTest() {
		TempEntity tempEntity = new TempEntity(Id.generateNextId(), OAuthType.GOOGLE);
		entityManager.persist(tempEntity);
	}

	@Entity
	@AllArgsConstructor
	@NoArgsConstructor
	static class TempEntity {

		@javax.persistence.Id
		private Id id;
		private OAuthType oauthType;
	}
}