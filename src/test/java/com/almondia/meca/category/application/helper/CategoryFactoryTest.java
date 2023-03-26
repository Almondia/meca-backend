package com.almondia.meca.category.application.helper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. 요청 속성에 맞게 인스턴스를 잘 생성했는 지 검증
 */
class CategoryFactoryTest {

	@Test
	@DisplayName("요청 속성에 맞게 인스턴스를 잘 생성했는 지 검증")
	void createEntityFromSaveCategoryRequestDtoTest() {
		SaveCategoryRequestDto saveCategoryRequestDto = SaveCategoryRequestDto.builder()
			.title(new Title("title"))
			.build();
		Id memberId = Id.generateNextId();
		Category category = CategoryFactory.genCategory(saveCategoryRequestDto, memberId);
		assertThat(category)
			.hasFieldOrProperty("categoryId")
			.hasFieldOrPropertyWithValue("title", saveCategoryRequestDto.getTitle())
			.hasFieldOrPropertyWithValue("memberId", memberId)
			.hasFieldOrProperty("isDeleted")
			.hasFieldOrProperty("isShared");
	}
}