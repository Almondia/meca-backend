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
	void shouldReturnFalseIfRoleIsNotNone() {
		Role role = Role.NONE;
		assertThat(role.hasRole()).isFalse();
	}
}