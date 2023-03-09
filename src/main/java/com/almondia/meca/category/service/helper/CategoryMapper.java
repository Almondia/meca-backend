package com.almondia.meca.category.service.helper;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;

public class CategoryMapper {

	public static CategoryResponseDto entityToCategoryResponseDto(Category category) {
		return CategoryResponseDto.builder()
			.categoryId(category.getCategoryId())
			.memberId(category.getMemberId())
			.title(category.getTitle())
			.isDeleted(category.isDeleted())
			.isShared(category.isShared())
			.createdAt(category.getCreatedAt())
			.modifiedAt(category.getModifiedAt())
			.build();
	}
}
