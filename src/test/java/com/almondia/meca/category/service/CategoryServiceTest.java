package com.almondia.meca.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.category.repository.CategoryRepository;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 카테고리 등록시 영속성 및 응답 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CategoryService.class})
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
}