package com.almondia.meca.common.domain.vo;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image implements Wrapper {

	private static final int MAX_LINK_LENGTH = 255;

	private String image;

	public Image(String image) {
		validateImages(image);
		this.image = image;
	}

	public static Image of(String image) {
		return new Image(image);
	}

	@Override
	public String toString() {
		return image;
	}

	private void validateImages(String image) {
		if (image.length() > MAX_LINK_LENGTH) {
			throw new IllegalArgumentException(String.format("image 링크 길이는 %d를 초과할 수 없습니다", MAX_LINK_LENGTH));
		}
	}
}
