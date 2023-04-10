package com.almondia.meca.card.controller.dto;

import java.util.List;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;

import lombok.Getter;

@Getter
public class CardCursorPageWithCategory extends CursorPage<CardResponseDto> {

	private CategoryResponseDto category;

	public CardCursorPageWithCategory(List<CardResponseDto> contents, Id hasNext, int pageSize, SortOrder sortOrder) {
		super(contents, hasNext, pageSize, sortOrder);
	}

	public void setCategory(Category category) {
		this.category = CategoryResponseDto.builder()
			.categoryId(category.getCategoryId())
			.title(category.getTitle())
			.thumbnail(category.getThumbnail())
			.createdAt(category.getCreatedAt())
			.modifiedAt(category.getModifiedAt())
			.memberId(category.getMemberId())
			.build();
	}
}
