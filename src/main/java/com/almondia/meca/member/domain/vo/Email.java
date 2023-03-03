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
public class Email implements Wrapper {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
	private String email;

	public Email(String email) {
		validateEmailFormat(email);
		this.email = email;
	}

	private void validateEmailFormat(String email) {
		Matcher matcher = EMAIL_PATTERN.matcher(email);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("이메일 형식과 일치하지 않습니다");
		}
	}

	@Override
	public String toString() {
		return email;
	}
}
