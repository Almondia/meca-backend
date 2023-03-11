package com.almondia.meca.category.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Title implements Wrapper, Comparable<Title> {

	private static final int MAX_LENGTH = 20;

	private String title;

	public Title(String title) {
		validateTitle(title);
		this.title = title;
	}

	private void validateTitle(String title) {
		if (title.length() > MAX_LENGTH) {
			throw new IllegalArgumentException(String.format("%d를 초과해서 제목을 지을 수 없습니다", MAX_LENGTH));
		}
		if (title.isBlank()) {
			throw new IllegalArgumentException("타이틀을 비우거나 공백만으로 초기화 할 수 없습니다");
		}
	}

	@Override
	public String toString() {
		return title;
	}

	@Override
	public int compareTo(Title o) {
		return this.title.compareTo(o.title);
	}
}
