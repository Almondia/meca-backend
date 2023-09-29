package com.almondia.meca.common.configuration.web;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.almondia.meca.category.infra.querydsl.CategorySortField;

class StringToCategorySortFieldConverterTest {

	@Test
	void convertTitleTest() {
		StringToCategorySortFieldConverter converter = new StringToCategorySortFieldConverter();
		CategorySortField title = converter.convert("title");
		assertThat(title).isEqualTo(CategorySortField.TITLE);
	}

	@Test
	void convertCreatedAtTest() {
		StringToCategorySortFieldConverter converter = new StringToCategorySortFieldConverter();
		CategorySortField createdAt = converter.convert("createdAt");
		assertThat(createdAt).isEqualTo(CategorySortField.CREATED_AT);
	}

	@Test
	void convertModifiedAtTest() {
		StringToCategorySortFieldConverter converter = new StringToCategorySortFieldConverter();
		CategorySortField updatedAt = converter.convert("modifiedAt");
		assertThat(updatedAt).isEqualTo(CategorySortField.MODIFIED_AT);
	}
}