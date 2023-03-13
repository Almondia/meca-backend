package com.almondia.meca.category.infra.querydsl;

import java.util.Arrays;

import com.almondia.meca.category.domain.entity.QCategory;
import com.querydsl.core.types.dsl.ComparableExpression;

public enum CategorySortField {
	TITLE("title", QCategory.category.title.title),
	CREATED_AT("createdAt", QCategory.category.createdAt),
	MODIFIED_AT("modifiedAt", QCategory.category.modifiedAt);

	private final String field;
	private final ComparableExpression<?> expression;

	CategorySortField(String field, ComparableExpression<?> expression) {
		this.field = field;
		this.expression = expression;
	}

	public static CategorySortField fromField(String field) {
		return Arrays.stream(values())
			.filter(sortField -> sortField.field.equals(field))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정렬 필드: " + field));
	}

	public ComparableExpression<?> getExpression() {
		return expression;
	}
}
