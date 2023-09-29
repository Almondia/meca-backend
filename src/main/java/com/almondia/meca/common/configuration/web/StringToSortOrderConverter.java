package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import com.almondia.meca.common.infra.querydsl.SortOrder;

public class StringToSortOrderConverter implements Converter<String, SortOrder> {

	@Override
	public SortOrder convert(@NonNull String source) {
		return SortOrder.fromString(source);
	}
}
