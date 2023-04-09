package com.almondia.meca.category.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.helper.CategoryTestHelper;

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

	/**
	 * 1. 사용자가 본인 권한 외에 카테고리를 호출하면 예외 발생 여부 테스트
	 * 2. 수정 요청에  title만 입력된 경우 title만 수정되는지 테스트
	 * 3. 수정 요청에  thumbnail만 입력된 경우 thumbnail만 수정되는지 테스트
	 * 4. 수정 요청에  isShared만 입력된 경우 isShared만 수정되는지 테스트
	 * 5. 수정 요청에  title, thumbnail, isShared 모두 입력된 경우 title, thumbnail, isShared 모두 수정되는지 테스트
	 */
	@Nested
	@DisplayName("카테고리 수정")
	@DataJpaTest
	@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	@Import({CategoryService.class, QueryDslConfiguration.class})
	class UpdateCategoryTest {

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

		@Autowired
		EntityManager entityManager;

		@Test
		@DisplayName("사용자가 본인 권한 외에 카테고리를 호출하면 예외 발생 여부 테스트")
		void updateCategoryWithNotOwnerTest() {
			// given
			Mockito.doThrow(AccessDeniedException.class)
				.when(categoryChecker)
				.checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(Title.of("title"))
				.thumbnail(Image.of("thumbnail"))
				.isShared(true)
				.build();

			// when
			assertThatThrownBy(() -> categoryService.updateCategory(updateCategoryRequestDto, Id.generateNextId(),
				Id.generateNextId()))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("수정 요청에 title만 입력된 경우 title만 수정되는지 테스트")
		void updateCategoryWithOnlyTitleTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(Title.of("update title"))
				.build();

			// when
			CategoryResponseDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("update title"));
			assertThat(updatedCategory.isShared()).isFalse();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("update title"), null, false);
		}

		@Test
		@DisplayName("수정 요청에 thumbnail만 입력된 경우 thumbnail만 수정되는지 테스트")
		void updateCategoryWithOnlyThumbnailTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.thumbnail(Image.of("update thumbnail"))
				.build();

			// when
			CategoryResponseDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("title"));
			assertThat(updatedCategory.getThumbnail()).isEqualTo(Image.of("update thumbnail"));
			assertThat(updatedCategory.isShared()).isFalse();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("title"), Image.of("update thumbnail"), false);
		}

		@Test
		@DisplayName("수정 요청에 isShared만 입력된 경우 isShared만 수정되는지 테스트")
		void updateCategoryWithOnlyIsSharedTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.isShared(true)
				.build();

			// when
			CategoryResponseDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("title"));
			assertThat(updatedCategory.getThumbnail()).isNull();
			assertThat(updatedCategory.isShared()).isTrue();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("title"), null, true);
		}

		@Test
		@DisplayName("수정 요청에 title, thumbnail, isShared 모두 입력된 경우 title, thumbnail, isShared 모두 수정되는지 테스트")
		void updateCategoryWithAllTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(Title.of("update title"))
				.thumbnail(Image.of("update thumbnail"))
				.isShared(true)
				.build();

			// when
			CategoryResponseDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("update title"));
			assertThat(updatedCategory.getThumbnail()).isEqualTo(Image.of("update thumbnail"));
			assertThat(updatedCategory.isShared()).isTrue();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("update title"), Image.of("update thumbnail"), true);
		}
	}

}