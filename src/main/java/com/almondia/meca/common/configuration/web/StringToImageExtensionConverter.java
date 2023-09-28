package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import com.almondia.meca.auth.s3.domain.vo.ImageExtension;

public class StringToImageExtensionConverter implements Converter<String, ImageExtension> {

	@Override
	public ImageExtension convert(@NonNull String source) {
		return ImageExtension.fromString(source);
	}
}

