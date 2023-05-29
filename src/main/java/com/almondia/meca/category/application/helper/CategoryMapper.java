package com.almondia.meca.category.application.helper;

import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.domain.entity.Category;

public class CategoryMapper {

	public static CategoryDto entityToCategoryDto(Category category) {
		return CategoryDto.builder()
			.categoryId(category.getCategoryId())
			.memberId(category.getMemberId())
			.title(category.getTitle())
			.thumbnail(category.getThumbnail())
			.isDeleted(category.isDeleted())
			.isShared(category.isShared())
			.createdAt(category.getCreatedAt())
			.modifiedAt(category.getModifiedAt())
			.build();
	}
}
