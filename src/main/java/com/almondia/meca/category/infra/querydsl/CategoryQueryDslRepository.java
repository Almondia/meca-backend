package com.almondia.meca.category.infra.querydsl;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.controller.dto.OffsetPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortField;
import com.almondia.meca.common.infra.querydsl.SortOption;

public interface CategoryQueryDslRepository {

	OffsetPage<CategoryResponseDto> findCategories(
		int offset,
		int pageSize,
		CategorySearchCriteria categorySearchCriteria,
		SortOption<? extends SortField> sortOption);

	CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId);

	CursorPage<SharedCategoryResponseDto> findCategoryShared(int pageSize, Id lastCategoryId);
}
