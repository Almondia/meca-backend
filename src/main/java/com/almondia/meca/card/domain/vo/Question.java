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
public class Question implements Wrapper {

	private static final int MAX_LENGTH = 51_000;

	@Lob
	private String question;

	public Question(String question) {
		validateQuestion(question);
		this.question = question;
	}

	public static Question of(String question) {
		return new Question(question);
	}

	@Override
	public String toString() {
		return question;
	}

	private void validateQuestion(String question) {
		if (question.isBlank()) {
			throw new IllegalArgumentException("퀴즈 문제에 비우거나 공백만 입력해서는 안됩니다");
		}
		if (question.length() > MAX_LENGTH) {
			throw new IllegalArgumentException(String.format("%d를 초과해서 입력하셨습니다", MAX_LENGTH));
		}
	}
}
