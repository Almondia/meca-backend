package com.almondia.meca.category.application.helper;

import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.domain.vo.Id;

public class CategoryFactory {

	public static Category genCategory(SaveCategoryRequestDto saveCategoryRequestDto, Id memberId) {
		return Category.builder()
			.categoryId(Id.generateNextId())
			.memberId(memberId)
			.title(saveCategoryRequestDto.getTitle())
			.build();
	}
}
