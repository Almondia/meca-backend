package com.almondia.meca.common.infra.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpression;

public class SortFactory {
	public static OrderSpecifier<?> createOrderSpecifier(SortOption<? extends SortField> sortOption) {
		ComparableExpression<?> expression = sortOption.getSortField().getExpression();
		if (sortOption.getSortOrder() == SortOrder.ASC) {
			return expression.asc();
		} else {
			return expression.desc();
		}
	}
}