package com.almondia.meca.member.domain.vo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name implements Wrapper {

	private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z가-힣]{1,20}$");
	private String name;

	public Name(String name) {
		validateNameFormat(name);
		this.name = name;
	}

	private void validateNameFormat(String name) {
		Matcher matcher = NAME_PATTERN.matcher(name);
		if (!matcher.find()) {
			throw new IllegalArgumentException("이름은 영문 또는 한글 1 ~ 20 사이만 입력 가능합니다");
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
