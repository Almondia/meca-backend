package com.almondia.meca.category.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.application.helper.CategoryFactory;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.member.domain.repository.MemberRepository;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest2 {

	@InjectMocks
	private CategoryService categoryService;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CardRepository cardRepository;

	@Mock
	private CardHistoryRepository cardHistoryRepository;

	@Mock
	private CategoryChecker categoryChecker;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CategoryRecommendRepository categoryRecommendRepository;

	@Nested
	@DisplayName("카테고리 등록 테스트")
	class SaveCategoryTest {

		@Test
		@DisplayName("카테고리 등록시 카테고리가 정상적으로 등록을 요청하는지 확인한다")
		void shouldCallRepositorySaveTest() {
			//given
			SaveCategoryRequestDto saveCategoryRequestDto = createSaveCategoryRequestDto();
			Id memberId = Id.generateNextId();

			when(categoryRepository.save(any(Category.class)))
				.thenReturn(CategoryFactory.genCategory(saveCategoryRequestDto, memberId));

			// when
			CategoryDto categoryDto = categoryService.saveCategory(saveCategoryRequestDto, memberId);

			// then
			verify(categoryRepository, times(1)).save(any(Category.class));
			assertThat(categoryDto.getTitle()).isEqualTo(saveCategoryRequestDto.getTitle());
			assertThat(categoryDto.getMemberId()).isEqualTo(memberId);
			assertThat(categoryDto.getThumbnail()).isEqualTo(saveCategoryRequestDto.getThumbnail());
		}

		@Test
		@DisplayName("카테고리 등록시 썸네일이 없어도 정상적으로 등록을 요청하는지 테스트")
		void shouldCallRepositorySaveTestExceptThumbnailTest() {
			//given
			SaveCategoryRequestDto saveCategoryRequestDto = createSaveCategoryRequestDtoExceptThumbnail();
			Id memberId = Id.generateNextId();

			when(categoryRepository.save(any(Category.class)))
				.thenReturn(CategoryFactory.genCategory(saveCategoryRequestDto, memberId));

			// when
			CategoryDto categoryDto = categoryService.saveCategory(saveCategoryRequestDto, memberId);

			// then
			verify(categoryRepository, times(1)).save(any(Category.class));
			assertThat(categoryDto.getTitle()).isEqualTo(saveCategoryRequestDto.getTitle());
			assertThat(categoryDto.getMemberId()).isEqualTo(memberId);
		}

		private SaveCategoryRequestDto createSaveCategoryRequestDto() {
			return SaveCategoryRequestDto.builder()
				.title(Title.of("카테고리 제목"))
				.thumbnail(Image.of("thumbnail"))
				.build();
		}

		private SaveCategoryRequestDto createSaveCategoryRequestDtoExceptThumbnail() {
			return SaveCategoryRequestDto.builder()
				.title(Title.of("카테고리 제목"))
				.build();
		}
	}

	@Nested
	@DisplayName("카테고리 수정 테스트")
	class UpdateCategoryTest {

		@Test
		void updateTitleTest() {
			// given
			UpdateCategoryRequestDto updateRequestDto = UpdateCategoryRequestDto.builder()
				.title(Title.of("수정된 카테고리 제목"))
				.build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class)))
				.thenReturn(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));

			// when
			CategoryDto categoryDto = categoryService.updateCategory(updateRequestDto, memberId, categoryId);

			// then
			assertThat(categoryDto.getTitle()).isEqualTo(updateRequestDto.getTitle());
		}

		@Test
		void updateThumbnailTest() {
			// given
			UpdateCategoryRequestDto updateRequestDto = UpdateCategoryRequestDto.builder()
				.thumbnail(Image.of("수정된 썸네일"))
				.build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class)))
				.thenReturn(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));

			// when
			CategoryDto categoryDto = categoryService.updateCategory(updateRequestDto, memberId, categoryId);

			// then
			assertThat(categoryDto.getThumbnail()).isEqualTo(updateRequestDto.getThumbnail());
		}

		@Test
		void updateSharedTest() {
			// given
			UpdateCategoryRequestDto updateRequestDto = UpdateCategoryRequestDto.builder()
				.shared(true)
				.build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class)))
				.thenReturn(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));

			// when
			CategoryDto categoryDto = categoryService.updateCategory(updateRequestDto, memberId, categoryId);

			// then
			assertThat(categoryDto.isShared()).isEqualTo(updateRequestDto.getShared());
		}
	}
}
