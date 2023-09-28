package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import com.almondia.meca.category.infra.querydsl.CategorySortField;

public class StringToCategorySortFieldConverter implements Converter<String, CategorySortField> {

	@Override
	public CategorySortField convert(@NonNull String source) {
		return CategorySortField.fromField(source);
	}
}
