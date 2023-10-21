package com.almondia.meca.card.domain.vo;

import java.util.Arrays;
import java.util.Set;
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
		makeKeywords();
	}

	public static KeywordAnswer valueOf(String keywordAnswer) {
		return new KeywordAnswer(keywordAnswer);
	}

	public boolean contains(String keyword) {
		if (keywords == null) {
			makeKeywords();
		}
		return keywords.contains(keyword.trim());
	}

	public boolean containsIgnoreCase(String keyword) {
		if (keywords == null) {
			makeKeywords();
		}
		return keywords.stream().anyMatch(keyword::equalsIgnoreCase);
	}

	@Override
	public String toString() {
		return keywordAnswer;
	}

	private void validateKeywordAnswer(String keywordAnswer) {
		Assert.isTrue(keywordAnswer.length() < MAX_LENGTH, () -> String.format("%d 이하로만 입력 가능합니다", MAX_LENGTH));
	}

	private void makeKeywords() {
		this.keywords = Arrays.stream(this.keywordAnswer.split(","))
			.map(String::trim)
			.collect(Collectors.toSet());
	}
}
