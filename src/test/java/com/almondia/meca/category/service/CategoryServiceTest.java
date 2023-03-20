package com.almondia.meca.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.category.service.checker.CategoryChecker;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. 카테고리 등록시 영속성 및 응답 테스트
 * 2. 카테고리 수정시 수정 여부 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CategoryService.class, CategoryChecker.class, QueryDslConfiguration.class})
class CategoryServiceTest {

	@Autowired
	CategoryService categoryService;

	@Autowired
	CategoryRepository categoryRepository;

	@Test
	@DisplayName("카테고리 등록시 영속성 및 응답 테스트")
	void test() {
		categoryService.saveCategory(SaveCategoryRequestDto.builder().title(new Title("title")).build(),
			Id.generateNextId());

		List<Category> all = categoryRepository.findAll();
		assertThat(all).isNotEmpty();
	}

	@Test
	@DisplayName("카테고리 수정시 수정 여부 테스트")
	void isModifiedWhenCallUpdateCategoryTest() {
		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		saveCategory(categoryId, memberId);
		CategoryResponseDto category = categoryService.updateCategory(
			UpdateCategoryRequestDto.builder().title(new Title("new title")).build(), categoryId, memberId);
		List<Category> all = categoryRepository.findAll();
		assertThat(all.get(0).getTitle()).isEqualTo(new Title("new title"));
	}

	@Test
	@DisplayName("카테고리 삭제시 권한 테스트")
	void checkAuthorityWheDeleteCategoryTest() {
		assertThatThrownBy(() -> categoryService.deleteCategory(Id.generateNextId(), Id.generateNextId())).isInstanceOf(
			AccessDeniedException.class);
	}

	private void saveCategory(Id categoryId, Id memberId) {
		categoryRepository.save(
			Category.builder().categoryId(categoryId).memberId(memberId).title(new Title("title")).build());
	}
}