package com.almondia.meca.common.configuration.web;

import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;

import lombok.NonNull;

public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

	@Override
	public LocalDateTime convert(@NonNull String source) {
		return LocalDateTime.parse(source);
	}
}
