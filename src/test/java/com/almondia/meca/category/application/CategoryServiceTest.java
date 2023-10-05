package com.almondia.meca.category.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.application.helper.CategoryFactory;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.CategoryStatisticsDto;
import com.almondia.meca.category.controller.dto.CategoryWithStatisticsResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryWithStatisticsAndRecommendDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.domain.service.CategoryInfoCombiner;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

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
	private CategoryInfoCombiner categoryInfoCombiner;

	@Nested
	@DisplayName("카테고리 등록 테스트")
	class SaveCategoryTest {

		@Test
		@DisplayName("카테고리 등록시 카테고리가 정상적으로 등록을 요청하는지 확인한다")
		void shouldCallRepositorySaveTest() {
			//given
			SaveCategoryRequestDto saveCategoryRequestDto = createSaveCategoryRequestDto();
			Id memberId = Id.generateNextId();

			when(categoryRepository.save(any(Category.class))).thenReturn(
				CategoryFactory.genCategory(saveCategoryRequestDto, memberId));

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

			when(categoryRepository.save(any(Category.class))).thenReturn(
				CategoryFactory.genCategory(saveCategoryRequestDto, memberId));

			// when
			CategoryDto categoryDto = categoryService.saveCategory(saveCategoryRequestDto, memberId);

			// then
			verify(categoryRepository, times(1)).save(any(Category.class));
			assertThat(categoryDto.getTitle()).isEqualTo(saveCategoryRequestDto.getTitle());
			assertThat(categoryDto.getMemberId()).isEqualTo(memberId);
		}

		private SaveCategoryRequestDto createSaveCategoryRequestDto() {
			return SaveCategoryRequestDto.builder().title(Title.of("카테고리 제목")).thumbnail(Image.of("thumbnail")).build();
		}

		private SaveCategoryRequestDto createSaveCategoryRequestDtoExceptThumbnail() {
			return SaveCategoryRequestDto.builder().title(Title.of("카테고리 제목")).build();
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
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class))).thenReturn(
				CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));

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
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class))).thenReturn(
				CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));

			// when
			CategoryDto categoryDto = categoryService.updateCategory(updateRequestDto, memberId, categoryId);

			// then
			assertThat(categoryDto.getThumbnail()).isEqualTo(updateRequestDto.getThumbnail());
		}

		@Test
		void updateSharedTest() {
			// given
			UpdateCategoryRequestDto updateRequestDto = UpdateCategoryRequestDto.builder().shared(true).build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class))).thenReturn(
				CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));

			// when
			CategoryDto categoryDto = categoryService.updateCategory(updateRequestDto, memberId, categoryId);

			// then
			assertThat(categoryDto.isShared()).isEqualTo(updateRequestDto.getShared());
		}
	}

	@Nested
	@DisplayName("카테고리 삭제 테스트")
	class DeleteCategoryTest {

		@Test
		@DisplayName("내 권한이 아닌 카테고리 요청시 예외 발생")
		void shouldThrowWhenCallNotMyCategoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class))).thenThrow(AccessDeniedException.class);

			// expect
			assertThatThrownBy(() -> categoryService.deleteCategory(memberId, categoryId)).isInstanceOf(
				AccessDeniedException.class);
		}

		@Test
		@DisplayName("내 권한인 카테고리 요청시 카테고리 삭제")
		void shouldDeleteCategoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			when(categoryChecker.checkAuthority(any(Id.class), any(Id.class))).thenReturn(
				CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));
			when(cardRepository.findByCategoryIdAndIsDeleted(any(Id.class), anyBoolean())).thenReturn(
				Collections.emptyList());
			when(cardHistoryRepository.findByCardIdInAndIsDeleted(anyList(), anyBoolean())).thenReturn(
				Collections.emptyList());

			// when
			categoryService.deleteCategory(memberId, categoryId);

			// then
			verify(categoryChecker, times(1)).checkAuthority(any(Id.class), any(Id.class));
			verify(cardRepository, times(1)).findByCategoryIdAndIsDeleted(any(Id.class), anyBoolean());
			verify(cardHistoryRepository, times(1)).findByCardIdInAndIsDeleted(anyList(), anyBoolean());
		}
	}

	@Nested
	@DisplayName("히스토리 응답을 포함한 개인 카테고리 커서 페이징 조회")
	class FindCursorPagingCategoryWithHistoryResponseTest {

		@Test
		@DisplayName("카테고리 컨텐츠가 비어있는 경우 빈 CursorPage 리턴")
		void shouldReturnEmptyCursorPageTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 1;
			Id lastCategoryId = Id.generateNextId();
			CategorySearchOption searchOption = makeEmptyCategorySearchOption();
			when(categoryInfoCombiner.findCategoryWithStatisticsResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class), any(), any(Id.class))).thenReturn(Collections.emptyList());

			// when
			CursorPage<CategoryWithStatisticsResponseDto> categoryCursorPage = categoryService.findCursorPagingCategoryWithHistoryResponse(
				pageSize, memberId, lastCategoryId, searchOption);

			// then
			assertThat(categoryCursorPage).isNotNull();
			assertThat(categoryCursorPage.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리 컨텐츠가 비어있지 않은 경우 CursorPage 리턴")
		void shouldReturnCursorPageTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			int pageSize = 1;
			Id lastCategoryId = Id.generateNextId();
			CategorySearchOption searchOption = makeEmptyCategorySearchOption();
			CategoryWithStatisticsResponseDto categoryWithStatisticsResponseDto = new CategoryWithStatisticsResponseDto(
				CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId),
				new CategoryStatisticsDto(0.0, 0, 0), 10L);
			when(categoryInfoCombiner.findCategoryWithStatisticsResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class), any(), any(Id.class))).thenReturn(
				List.of(categoryWithStatisticsResponseDto));

			// when
			CursorPage<CategoryWithStatisticsResponseDto> categoryCursorPage = categoryService.findCursorPagingCategoryWithHistoryResponse(
				pageSize, memberId, lastCategoryId, searchOption);

			// then
			verify(categoryInfoCombiner, times(1)).findCategoryWithStatisticsResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class), any(), any(Id.class));
			assertThat(categoryCursorPage).isNotNull();
			assertThat(categoryCursorPage.getContents()).isNotEmpty();

		}

		private CategorySearchOption makeEmptyCategorySearchOption() {
			return CategorySearchOption.builder().build();
		}

		private CategoryDto makeCategoryDto(Id categoryId, Id memberId, boolean shared) {
			return CategoryDto.builder()
				.categoryId(categoryId)
				.memberId(memberId)
				.thumbnail(Image.of("썸네일"))
				.title(Title.of("제목"))
				.isDeleted(false)
				.isShared(shared)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
		}
	}

	@Nested
	@DisplayName("공유 카테고리 커서 페이징 조회 테스트")
	class FindCursorPagingSharedCategoryResponseDtoTest {

		@Test
		@DisplayName("공유 카테고리 컨텐츠가 비어있는 경우 빈 CursorPage 리턴")
		void shouldReturnEmptyCursorPageTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 1;
			Id lastCategoryId = Id.generateNextId();
			CategorySearchOption searchOption = makeEmptyCategorySearchOption();
			when(categoryInfoCombiner.findSharedCategoryResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class))).thenReturn(Collections.emptyList());

			// when
			CursorPage<SharedCategoryResponseDto> categoryCursorPage = categoryService.findCursorPagingSharedCategoryResponseDto(
				pageSize, lastCategoryId, searchOption);

			// then
			assertThat(categoryCursorPage).isNotNull();
			assertThat(categoryCursorPage.getContents()).isEmpty();
		}

		@Test
		@DisplayName("공유 카테고리 컨텐츠가 비어있지 않은 경우 CursorPage 리턴")
		void shouldReturnCursorPageTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			int pageSize = 1;
			Id lastCategoryId = Id.generateNextId();
			CategorySearchOption searchOption = makeEmptyCategorySearchOption();
			SharedCategoryResponseDto sharedCategoryResponseDto = new SharedCategoryResponseDto(
				CategoryTestHelper.generateSharedCategory("title", memberId, categoryId),
				MemberTestHelper.generateMember(memberId));
			when(categoryInfoCombiner.findSharedCategoryResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class))).thenReturn(
				List.of(sharedCategoryResponseDto));

			// when
			CursorPage<SharedCategoryResponseDto> categoryCursorPage = categoryService.findCursorPagingSharedCategoryResponseDto(
				pageSize, lastCategoryId, searchOption);

			// then
			verify(categoryInfoCombiner, times(1)).findSharedCategoryResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class));
			assertThat(categoryCursorPage).isNotNull();
			assertThat(categoryCursorPage.getContents()).isNotEmpty();
		}

		private CategorySearchOption makeEmptyCategorySearchOption() {
			return CategorySearchOption.builder().build();
		}
	}

	@Nested
	@DisplayName("공유 카테고리 히스토리 커서 페이징 조회 테스트")
	class FindSharedCategoryWithStatisticsTest {

		@Test
		@DisplayName("공유 카테고리 컨텐츠가 비어있는 경우 빈 CursorPage 리턴")
		void shouldReturnEmptyCursorPageTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 1;
			Id lastCategoryId = Id.generateNextId();
			CategorySearchOption searchOption = makeEmptyCategorySearchOption();
			when(categoryInfoCombiner.findSharedCategoryWithStatisticsResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class), any(Id.class))).thenReturn(Collections.emptyList());

			// when
			CursorPage<SharedCategoryWithStatisticsAndRecommendDto> categoryCursorPage = categoryService.findSharedCategoryWithStatistics(
				pageSize, lastCategoryId, searchOption, memberId);

			// then
			assertThat(categoryCursorPage).isNotNull();
			assertThat(categoryCursorPage.getContents()).isEmpty();
		}

		@Test
		@DisplayName("공유 카테고리 컨텐츠가 비어있지 않은 경우 CursorPage 리턴")
		void shouldReturnCursorPageTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			int pageSize = 1;
			Id lastCategoryId = Id.generateNextId();
			CategorySearchOption searchOption = makeEmptyCategorySearchOption();
			SharedCategoryWithStatisticsAndRecommendDto sharedCategoryWithStatisticsAndRecommendDto = new SharedCategoryWithStatisticsAndRecommendDto(
				CategoryTestHelper.generateSharedCategory("title", memberId, categoryId),
				MemberTestHelper.generateMember(memberId), new CategoryStatisticsDto(0.0, 0, 0), 10L);
			when(categoryInfoCombiner.findSharedCategoryWithStatisticsResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class), any(Id.class))).thenReturn(
				List.of(sharedCategoryWithStatisticsAndRecommendDto));

			// when
			CursorPage<SharedCategoryWithStatisticsAndRecommendDto> categoryCursorPage = categoryService.findSharedCategoryWithStatistics(
				pageSize, lastCategoryId, searchOption, memberId);

			// then
			verify(categoryInfoCombiner, times(1)).findSharedCategoryWithStatisticsResponse(anyInt(), any(Id.class),
				any(CategorySearchOption.class), any(Id.class));
			assertThat(categoryCursorPage).isNotNull();
			assertThat(categoryCursorPage.getContents()).isNotEmpty();
		}

		private CategorySearchOption makeEmptyCategorySearchOption() {
			return CategorySearchOption.builder().build();
		}
	}
}
