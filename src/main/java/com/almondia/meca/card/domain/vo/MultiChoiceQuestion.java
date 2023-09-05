package com.almondia.meca.card.domain.vo;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

import org.json.JSONArray;
import org.json.JSONException;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class MultiChoiceQuestion implements Wrapper {

	private static final int MAX_CHOICE_COUNT = 5;
	private static final int MIN_CHOICE_COUNT = 2;
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
		validateStringLength(value);
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(value);
		} catch (JSONException e) {
			throw new IllegalArgumentException("JSON 형식이 아닙니다");
		}
		validateChoiceLength(jsonArray);
		validateViewLength(jsonArray);
	}

	private void validateStringLength(String value) {
		if (value.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("최대 " + MAX_LENGTH + "자까지 입력 가능합니다");
		}
	}

	private void validateChoiceLength(JSONArray jsonArray) {
		int choiceViewWithoutQuestionLength = jsonArray.length() - 1;
		if (choiceViewWithoutQuestionLength < MIN_CHOICE_COUNT) {
			throw new IllegalArgumentException("선택지는 최소 " + MIN_CHOICE_COUNT + "개 이상이어야 합니다");
		}
		if (choiceViewWithoutQuestionLength > MAX_CHOICE_COUNT) {
			throw new IllegalArgumentException("선택지는 최대 " + MAX_CHOICE_COUNT + "개 이하여야 합니다");
		}
	}

	private void validateViewLength(JSONArray jsonArray) {
		for (int i = 1; i < jsonArray.length(); ++i) {
			String view = jsonArray.getString(i);
			if (view.length() > MAX_LENGTH_PER_VIEW_QUESTION) {
				throw new IllegalArgumentException("보기는 최대 " + MAX_LENGTH_PER_VIEW_QUESTION + "자까지 입력 가능합니다");
			}
			if (view.isBlank()) {
				throw new IllegalArgumentException("보기는 비어 있을 수 없습니다");
			}
		}
	}
}
