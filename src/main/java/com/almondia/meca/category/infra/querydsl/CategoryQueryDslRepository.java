package com.almondia.meca.category.infra.querydsl;

import java.util.List;

import org.springframework.lang.Nullable;

import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CategoryQueryDslRepository {

	CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId);

	CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId, CategorySearchOption categorySearchOption);

	/**
	 * @param shared shared가 null이면 모든 카테고리, true이면 공유 카테고리, false이면 공유하지 않은 카테고리 조회
	 */
	List<Category> findCategories(int pageSize, @Nullable Id lastCategoryId,
		CategorySearchOption categorySearchOption, @Nullable Boolean shared);
}
