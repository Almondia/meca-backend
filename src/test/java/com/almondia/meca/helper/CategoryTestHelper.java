package com.almondia.meca.helper;

import java.time.LocalDateTime;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

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
}
