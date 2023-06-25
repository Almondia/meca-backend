package com.almondia.meca.card.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EssayAnswer implements Wrapper {

	private static final int MAX_LENGTH = 255;

	private String essayAnswer;

	public EssayAnswer(String essayAnswer) {
		validateEssayAnswer(essayAnswer);
		this.essayAnswer = essayAnswer;
	}

	public static EssayAnswer valueOf(String essayAnswer) {
		return new EssayAnswer(essayAnswer);
	}

	@Override
	public String toString() {
		return essayAnswer;
	}

	private void validateEssayAnswer(String essayAnswer) {
		if (essayAnswer.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("%d 초과해서 문자열 길이를 늘릴 수 없습니다");
		}
	}
}
