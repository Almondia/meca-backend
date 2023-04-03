package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;

import com.almondia.meca.auth.s3.domain.vo.Purpose;

import lombok.NonNull;

public class StringToPurposeConverter implements Converter<String, Purpose> {
	@Override
	public Purpose convert(@NonNull String source) {
		return Purpose.ofDetails(source);
	}
}
