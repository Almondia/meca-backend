package com.almondia.meca.member.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name implements Wrapper {

	private static final int MAX_LENGTH = 20;
	private static final int MIN_LENGTH = 1;
	private String name;

	private Name(String name) {
		validateNameFormat(name);
		this.name = name;
	}

	public static Name of(String name) {
		return new Name(name);
	}

	@Override
	public String toString() {
		return name;
	}

	private void validateNameFormat(String name) {
		name = name.strip();
		if (name.isBlank()) {
			throw new IllegalArgumentException("빈 공백을 입력할 수 없습니다");
		}
		if (name.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("이름은 1 ~ 20 사이만 입력 가능합니다");
		}

		if (name.length() < MIN_LENGTH) {
			throw new IllegalArgumentException("이름은 1 ~ 20 사이만 입력 가능합니다");
		}
	}
}
