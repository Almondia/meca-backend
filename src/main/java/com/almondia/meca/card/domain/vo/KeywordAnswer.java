package com.almondia.meca.card.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordAnswer implements Wrapper {

	private static final int MAX_LENGTH = 255;

	private String keywordAnswer;

	public KeywordAnswer(String keywordAnswer) {
		validateKeywordAnswer(keywordAnswer);
		this.keywordAnswer = keywordAnswer;
	}

	private void validateKeywordAnswer(String keywordAnswer) {
		if (keywordAnswer.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("%d 초과해서 문자열 길이를 늘릴 수 없습니다");
		}
	}

	@Override
	public String toString() {
		return keywordAnswer;
	}
}
