package com.almondia.meca.category.application.helper;

import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CategoryFactory {

	public static Category genCategory(SaveCategoryRequestDto saveCategoryRequestDto, Id memberId) {
		Image image = saveCategoryRequestDto.getThumbnail();
		return Category.builder()
			.categoryId(Id.generateNextId())
			.memberId(memberId)
			.title(saveCategoryRequestDto.getTitle())
			.thumbnail(image)
			.build();
	}
}
