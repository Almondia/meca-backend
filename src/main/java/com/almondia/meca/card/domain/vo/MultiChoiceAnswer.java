package com.almondia.meca.card.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MultiChoiceAnswer implements Wrapper {

	private static final int MAX_NUMBER = 5;
	private static final int MIN_NUMBER = 1;

	private Integer number;

	public MultiChoiceAnswer(int number) {
		validateNumber(number);
		this.number = number;
	}

	public static MultiChoiceAnswer valueOf(String number) {
		return new MultiChoiceAnswer(Integer.parseInt(number));
	}

	public String getText() {
		return number.toString();
	}

	@Override
	public String toString() {
		return number.toString();
	}

	private void validateNumber(int number) {
		if (number < MIN_NUMBER || number > MAX_NUMBER) {
			throw new IllegalArgumentException(String.format("%d부터 %d숫자만 입력 가능합니다", MIN_NUMBER, MAX_NUMBER));
		}
	}
}
