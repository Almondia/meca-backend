package com.almondia.meca.helper;

import java.time.LocalDateTime;

import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;

public class CategoryTestHelper {

	public static Category generateUnSharedCategory(String title, Id memberId, Id categoryId) {
		return Category.builder()
			.categoryId(categoryId)
			.memberId(memberId)
			.title(Title.of(title))
			.isDeleted(false)
			.isShared(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static Category generateSharedCategory(String title, Id memberId, Id categoryId) {
		return Category.builder()
			.categoryId(categoryId)
			.memberId(memberId)
			.title(Title.of(title))
			.isDeleted(false)
			.isShared(true)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static SaveCategoryRequestDto generateSaveCategoryRequestDto() {
		return SaveCategoryRequestDto.builder()
			.title(Title.of("title"))
			.thumbnail(new Image("https://aws.s3.com"))
			.build();
	}

	public static CategoryDto generateCategoryResponseDto() {
		return CategoryDto.builder()
			.categoryId(Id.generateNextId())
			.memberId(Id.generateNextId())
			.thumbnail(new Image("https://aws.s3.com"))
			.title(Title.of("title"))
			.isDeleted(false)
			.isShared(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}
}
