package com.almondia.meca.card.domain.vo;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;

import org.springframework.util.Assert;

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
	private transient Set<String> keywords;

	public KeywordAnswer(String keywordAnswer) {
		validateKeywordAnswer(keywordAnswer);
		this.keywordAnswer = keywordAnswer;
		try {
			this.keywords = Arrays.stream(this.keywordAnswer.split(","))
				.map(String::trim)
				.collect(Collectors.toSet());
		} catch (PatternSyntaxException patternSyntaxException) {
			throw new IllegalArgumentException("키워드를 분리할 수 없습니다. 키워드는 ,로 구분되어야 합니다.");
		}
	}

	public static KeywordAnswer valueOf(String keywordAnswer) {
		return new KeywordAnswer(keywordAnswer);
	}

	public boolean contains(String keyword) {
		return keywords.contains(keyword.trim());
	}

	@Override
	public String toString() {
		return keywordAnswer;
	}

	private void validateKeywordAnswer(String keywordAnswer) {
		Assert.isTrue(keywordAnswer.length() < MAX_LENGTH, () -> String.format("%d 이하로만 입력 가능합니다", MAX_LENGTH));
	}
}
