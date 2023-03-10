package com.almondia.meca.category.infra.querydsl;

import com.almondia.meca.common.domain.vo.SortOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategorySortOption {
	private final CategorySortField sortField;
	private final SortOrder sortOrder;

	public static CategorySortOption of(String sortField, String sortOrder) {
		CategorySortField newSortField = CategorySortField.fromField(sortField);
		SortOrder newSortOrder = SortOrder.valueOf(sortOrder.toUpperCase());
		return new CategorySortOption(newSortField, newSortOrder);
	}

	public static CategorySortOption of(CategorySortField sortField, SortOrder sortOrder) {
		return new CategorySortOption(sortField, sortOrder);
	}
}
