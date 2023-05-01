package com.almondia.meca.cardhistory.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer implements Wrapper {

	private static final int MAX_LENGTH = 255;

	private String answer;

	public Answer(String answer) {
		validateAnswer(answer);
		this.answer = answer;
	}

	@Override
	public String toString() {
		return answer;
	}

	private void validateAnswer(String answer) {
		if (answer.length() > MAX_LENGTH) {
			throw new IllegalArgumentException(String.format("%d 초과로 입력할 수 없습니다", MAX_LENGTH));
		}
	}
}
