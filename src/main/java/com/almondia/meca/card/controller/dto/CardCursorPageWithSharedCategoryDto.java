package com.almondia.meca.card.controller.dto;

import java.util.List;

import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;

import lombok.Getter;

@Getter
public class CardCursorPageWithSharedCategoryDto extends CursorPage<SharedCardResponseDto> {

	private CategoryDto category;

	public CardCursorPageWithSharedCategoryDto(List<SharedCardResponseDto> contents, Id hasNext, int pageSize,
		SortOrder sortOrder) {
		super(contents, hasNext, pageSize, sortOrder);
	}

	public void setCategory(Category category) {
		this.category = CategoryMapper.entityToCategoryResponseDto(category);
	}
}
