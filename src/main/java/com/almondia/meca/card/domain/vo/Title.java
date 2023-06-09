package com.almondia.meca.card.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Title implements Wrapper {

	private static final int TITLE_MAX_LENGTH = 40;
	private static final int TITLE_MIN_LENGTH = 2;

	private String title;

	public Title(String title) {
		validateTitle(title);
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}

	public static Title of(String title) {
		return new Title(title);
	}

	private void validateTitle(String title) {
		if (title.length() < TITLE_MIN_LENGTH || title.length() > TITLE_MAX_LENGTH) {
			throw new IllegalArgumentException(
				String.format("%d 길이이상 %d 길이 이하로 입력해주세요", TITLE_MIN_LENGTH, TITLE_MAX_LENGTH));
		}
		if (title.isBlank()) {
			throw new IllegalArgumentException("공백만 입력할 수 업습니다");
		}
	}
}
