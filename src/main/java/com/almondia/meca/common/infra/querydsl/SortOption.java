package com.almondia.meca.common.infra.querydsl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SortOption<F extends SortField> {
	private final F sortField;
	private final SortOrder sortOrder;

	public static <F extends SortField> SortOption<F> of(F sortField, SortOrder sortOrder) {
		return new SortOption<>(sortField, sortOrder);
	}
}
