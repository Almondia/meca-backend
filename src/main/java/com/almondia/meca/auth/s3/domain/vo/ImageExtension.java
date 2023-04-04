package com.almondia.meca.auth.s3.domain.vo;

import lombok.Getter;

@Getter
public enum ImageExtension {
	PNG("png"),
	JPEG("jpeg"),
	JPG("jpg"),
	GIF("gif");

	private final String extension;

	ImageExtension(String extension) {
		this.extension = extension;
	}

	public static ImageExtension fromString(String extension) {
		for (ImageExtension imageExtension : ImageExtension.values()) {
			if (imageExtension.extension.equals(extension)) {
				return imageExtension;
			}
		}
		throw new IllegalArgumentException("Invalid extension: " + extension);
	}
}
