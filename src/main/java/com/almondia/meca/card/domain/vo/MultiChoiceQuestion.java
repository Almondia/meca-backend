package com.almondia.meca.card.domain.vo;

import java.util.regex.Pattern;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class MultiChoiceQuestion implements Wrapper {

	private static final Pattern MULTI_CHOICE_QUESTION_PATTERN = Pattern.compile("^\\[.*]$");
	private static final String SPLITTER = ",";
	private static final int MAX_CHOICE_COUNT = 5;
	private static final int MIN_CHOICE_COUNT = 1;
	private static final int MAX_LENGTH_PER_VIEW_QUESTION = 100;

	private static final int MAX_LENGTH = 51_000;

	@Lob
	private String value;

	public MultiChoiceQuestion(String value) {
		validateQuestion(value);
		this.value = value;
	}

	public static MultiChoiceQuestion of(String value) {
		return new MultiChoiceQuestion(value);
	}

	@Override
	public String toString() {
		return value;
	}

	private void validateQuestion(String value) {
		if (value.isBlank()) {
			throw new IllegalArgumentException("퀴즈 문제에 비우거나 공백만 입력해서는 안됩니다");
		}
		if (value.length() > MAX_LENGTH) {
			throw new IllegalArgumentException(String.format("%d를 초과해서 입력하셨습니다", MAX_LENGTH));
		}
		if (!MULTI_CHOICE_QUESTION_PATTERN.matcher(value).matches()) {
			throw new IllegalArgumentException("퀴즈 문제는 [로 시작해서 ]로 끝나야 합니다");
		}
		String[] split = value.split(SPLITTER);
		if (split.length > MAX_CHOICE_COUNT + 1 || split.length < MIN_CHOICE_COUNT + 1) {
			throw new IllegalArgumentException(
				String.format("퀴즈 문제의 선택지는 %d개 이상 %d개 이하로 입력해야 합니다", MIN_CHOICE_COUNT, MAX_CHOICE_COUNT));
		}
		for (String choice : split) {
			if (choice.length() > MAX_LENGTH_PER_VIEW_QUESTION) {
				throw new IllegalArgumentException(
					String.format("퀴즈 문제의 선택지는 %d자 이하로 입력해야 합니다", MAX_LENGTH_PER_VIEW_QUESTION));
			}
		}
	}
}
