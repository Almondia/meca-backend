package com.almondia.meca.card.domain.vo;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Description implements Wrapper {

	private static final int MAX_LENGTH = 51_000;

	@Lob
	private String description;

	public Description(String description) {
		validateEditText(description);
		this.description = description;
	}

	public static Description of(String description) {
		return new Description(description);
	}

	private void validateEditText(String editText) {
		if (editText.length() > MAX_LENGTH) {
			throw new IllegalArgumentException(String.format("%d 초과해서 문자열 길이를 늘릴 수 없습니다", MAX_LENGTH));
		}
	}

	@Override
	public String toString() {
		return description;
	}
}
