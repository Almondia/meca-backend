package com.almondia.meca.category.infra.querydsl;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.common.controller.dto.OffsetPage;
import com.almondia.meca.common.infra.querydsl.SortField;
import com.almondia.meca.common.infra.querydsl.SortOption;

public interface CategoryQueryDslRepository {

	OffsetPage<CategoryResponseDto> findCategories(
		int offset,
		int pageSize,
		CategorySearchCriteria categorySearchCriteria,
		SortOption<? extends SortField> sortOption);
}
