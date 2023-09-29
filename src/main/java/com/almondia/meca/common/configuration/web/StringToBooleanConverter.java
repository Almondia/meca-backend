package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class StringToBooleanConverter implements Converter<String, Boolean> {

	@Override
	public Boolean convert(@NonNull String source) {
		return Boolean.parseBoolean(source);
	}
}
