package com.almondia.meca.category.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;

class CategoryServiceTest {

	/**
	 * 1. 카테고리 등록시 영속성 테스트
	 * 2. 카테고리 thubnail 이미지가 없어도 등록 가능 여부 테스트
	 * 3. title이 null이면 등록 불가능 여부 테스트
	 */
	@Nested
	@DisplayName("카테고리 등록")
	@DataJpaTest
	@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	@Import({CategoryService.class, QueryDslConfiguration.class})
	class SaveCategoryTest {

		@Autowired
		CategoryRepository categoryRepository;

		@MockBean
		CategoryChecker categoryChecker;

		@MockBean
		CardRepository cardRepository;

		@MockBean
		CardHistoryRepository cardHistoryRepository;

		@Autowired
		CategoryService categoryService;

		@Test
		@DisplayName("카테고리 등록시 영속성 테스트")
		void saveCategoryTest() {
			// given
			SaveCategoryRequestDto saveCategoryRequestDto = SaveCategoryRequestDto.builder()
				.title(Title.of("title"))
				.thumbnail(Image.of("thumbnail"))
				.build();

			// when
			CategoryResponseDto result = categoryService.saveCategory(saveCategoryRequestDto,
				Id.generateNextId());

			// then
			Category category = categoryRepository.findAll().get(0);
			assertThat(category.getTitle()).isEqualTo(Title.of("title"));
			assertThat(category.getThumbnail()).isEqualTo(Image.of("thumbnail"));
			assertThat(result).extracting("title", "thumbnail")
				.containsExactly(Title.of("title"), Image.of("thumbnail"));
		}

		@Test
		@DisplayName("카테고리 thubnail 이미지가 없어도 등록 가능 여부 테스트")
		void saveCategoryWithoutThumbnailTest() {
			// given
			SaveCategoryRequestDto saveCategoryRequestDto = SaveCategoryRequestDto.builder()
				.title(Title.of("title"))
				.build();

			// when
			CategoryResponseDto result = categoryService.saveCategory(saveCategoryRequestDto,
				Id.generateNextId());

			// then
			Category category = categoryRepository.findAll().get(0);
			assertThat(category.getTitle()).isEqualTo(Title.of("title"));
			assertThat(category.getThumbnail()).isNull();
			assertThat(result)
				.extracting("title", "thumbnail")
				.containsExactly(Title.of("title"), null);
		}

		@Test
		@DisplayName("title이 null이면 등록 불가능 여부 테스트")
		void saveCategoryWithNullTitleTest() {
			// given
			SaveCategoryRequestDto saveCategoryRequestDto = SaveCategoryRequestDto.builder()
				.thumbnail(Image.of("thumbnail"))
				.build();

			// when
			assertThatThrownBy(() -> categoryService.saveCategory(saveCategoryRequestDto,
				Id.generateNextId()))
				.isInstanceOf(DataIntegrityViolationException.class);
		}
	}

}