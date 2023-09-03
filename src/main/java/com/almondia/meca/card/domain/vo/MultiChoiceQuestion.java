package com.almondia.meca.card.domain.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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

	private static final Pattern MULTI_CHOICE_QUESTION_PATTERN_1 = Pattern.compile("^\\[.*]$");
	private static final Pattern MULTI_CHOICE_QUESTION_PATTERN_2 = Pattern.compile("\\\\\"(.*?)\\\\\"");
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
		if (!MULTI_CHOICE_QUESTION_PATTERN_1.matcher(value).matches()) {
			throw new IllegalArgumentException("퀴즈 문제는 [로 시작해서 ]로 끝나야 합니다");
		}
		Matcher matcher = MULTI_CHOICE_QUESTION_PATTERN_2.matcher(value);
		List<String> choices = new ArrayList<>();
		while (matcher.find()) {
			choices.add(matcher.group());
		}
		for (int i = 1; i < choices.size(); i++) {
			String choice = choices.get(i);
			if (choice.length() > MAX_LENGTH_PER_VIEW_QUESTION) {
				throw new IllegalArgumentException(
					String.format("퀴즈 문제의 선택지는 %d자를 초과할 수 없습니다", MAX_LENGTH_PER_VIEW_QUESTION));
			}
		}
		if (choices.size() - 1 > MAX_CHOICE_COUNT) {
			throw new IllegalArgumentException(String.format("퀴즈 문제의 선택지는 %d개를 초과할 수 없습니다", MAX_CHOICE_COUNT));
		}
		if (choices.size() - 1 < MIN_CHOICE_COUNT) {
			throw new IllegalArgumentException(String.format("퀴즈 문제의 선택지는 %d개 이상이어야 합니다", MIN_CHOICE_COUNT));
		}

	}
}
