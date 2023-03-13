package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;

import com.almondia.meca.category.infra.querydsl.CategorySortField;

import lombok.NonNull;

public class StringToCategorySortFieldConverter implements Converter<String, CategorySortField> {

	@Override
	public CategorySortField convert(@NonNull String source) {
		return CategorySortField.fromField(source);
	}
}
