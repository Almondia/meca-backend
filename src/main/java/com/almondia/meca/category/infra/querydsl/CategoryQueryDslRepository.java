package com.almondia.meca.category.infra.querydsl;

import java.util.List;

import org.springframework.lang.Nullable;

import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CategoryQueryDslRepository {

	CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId);

	CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId, CategorySearchOption categorySearchOption);

	CursorPage<SharedCategoryResponseDto> findCategoryShared(int pageSize, Id lastCategoryId);

	List<Category> findSharedCategories(int pageSize, @Nullable Id lastCategoryId,
		CategorySearchOption categorySearchOption);
}
