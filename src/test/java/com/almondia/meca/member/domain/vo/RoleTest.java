package com.almondia.meca.member.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * hasRole 동작 검증
 */
class RoleTest {

	@Test
	@DisplayName("Role이 None인 경우 hasRole()은 False를 리턴한다")
	void shouldReturnFalseIfRoleIsNotNoneTest() {
		Role role = Role.NONE;
		assertThat(role.hasRole()).isFalse();
	}

	@Test
	@DisplayName("Role이 ADMIN hasRole()은 True를 리턴한다")
	void shouldReturnTrueIfRoleIsAdminTest() {
		Role role = Role.ADMIN;
		assertThat(role.hasRole()).isTrue();
	}

	@Test
	@DisplayName("Role이 User인 경우 hasRole()은 True를 리턴한다")
	void shouldReturnTrueIfRoleIsUserTest() {
		Role role = Role.USER;
		assertThat(role.hasRole()).isTrue();
	}
}