package com.almondia.meca.cardhistory.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score implements Wrapper {

	private static final int MAX_SCORE = 100;
	private static final int MIN_SCORE = 0;

	private int score;

	public Score(int score) {
		validateScore(score);
		this.score = score;
	}

	@Override
	public String toString() {
		return String.valueOf(score);
	}

	private void validateScore(int score) {
		if (score > MAX_SCORE) {
			throw new IllegalArgumentException(String.format("%d 점수를 초과할 수 없습니다", MAX_SCORE));
		}
		if (score < MIN_SCORE) {
			throw new IllegalArgumentException("점수는 음수가 될 수 없습니다");
		}
	}
}
