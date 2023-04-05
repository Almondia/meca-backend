package com.almondia.meca.card.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditText implements Wrapper {

	private static final int MAX_LENGTH = 2_1000;

	private String editText;

	public EditText(String editText) {
		validateEditText(editText);
		this.editText = editText;
	}

	private void validateEditText(String editText) {
		if (editText.length() > MAX_LENGTH) {
			throw new IllegalArgumentException(String.format("%d 초과해서 문자열 길이를 늘릴 수 없습니다", MAX_LENGTH));
		}
		if (editText.isBlank()) {
			throw new IllegalArgumentException("빈 공백은 허용되지 않습니다");
		}
	}

	@Override
	public String toString() {
		return editText;
	}
}
