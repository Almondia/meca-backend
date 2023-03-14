package com.almondia.meca.category.infra.querydsl;

import com.almondia.meca.common.infra.querydsl.SortField;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpression;

public class CategorySortFactory {
	public static OrderSpecifier<?> createOrderSpecifier(SortOption<? extends SortField> sortOption) {
		ComparableExpression<?> expression = sortOption.getSortField().getExpression();
		if (sortOption.getSortOrder() == SortOrder.ASC) {
			return expression.asc();
		} else {
			return expression.desc();
		}
	}
}