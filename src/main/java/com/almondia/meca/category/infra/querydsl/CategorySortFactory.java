package com.almondia.meca.category.infra.querydsl;

import com.almondia.meca.common.domain.vo.SortOrder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpression;

public class CategorySortFactory {
	public static OrderSpecifier<?> createOrderSpecifier(CategorySortOption sortOption) {
		ComparableExpression<?> expression = sortOption.getSortField().getExpression();
		if (sortOption.getSortOrder() == SortOrder.ASC) {
			return expression.asc();
		} else {
			return expression.desc();
		}
	}
}
