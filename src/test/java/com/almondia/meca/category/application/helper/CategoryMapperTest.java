package com.almondia.meca.category.application.helper;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. 성공적으로 응답 포맷으로 변환해야 함
 */
class CategoryMapperTest {

	@Test
	@DisplayName("성공적으로 응답 포맷으로 변환해야 함")
	void shouldReturnResponseFormatWhenCallToCategoryResponseDtoTest() {
		CategoryDto dto = CategoryMapper.entityToCategoryResponseDto(makeCategory());
		assertThat(dto)
			.hasFieldOrProperty("categoryId")
			.hasFieldOrProperty("memberId")
			.hasFieldOrProperty("title")
			.hasFieldOrProperty("isDeleted")
			.hasFieldOrProperty("isShared")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
	}

	private Category makeCategory() {
		return Category.builder()
			.memberId(Id.generateNextId())
			.categoryId(Id.generateNextId())
			.title(new Title("title"))
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}
}