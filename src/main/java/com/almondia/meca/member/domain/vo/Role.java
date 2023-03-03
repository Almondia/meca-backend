package com.almondia.meca.member.domain.vo;

import lombok.Getter;

@Getter
public enum Role {
	ADMIN("ROLE_ADMIN"),
	USER("ROLE_USER"),
	NONE("NONE");

	private final String details;

	Role(String details) {
		this.details = details;
	}

	public boolean hasRole() {
		return !this.equals(NONE);
	}
}
