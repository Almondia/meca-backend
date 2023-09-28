package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import com.almondia.meca.common.domain.vo.Id;

public class StringToIdConverter implements Converter<String, Id> {

	@Override
	public Id convert(@NonNull String source) {
		return new Id(source);
	}
}
