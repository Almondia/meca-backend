package com.almondia.meca.category.infra.querydsl;

import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CategoryQueryDslRepository {

	CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId);

	CursorPage<SharedCategoryResponseDto> findCategoryShared(int pageSize, Id lastCategoryId);
}
