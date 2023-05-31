package com.almondia.meca.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.category.application.CategoryRecommendService;
import com.almondia.meca.category.application.CategoryService;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.CategoryRecommendCheckDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	private final CategoryRecommendService categoryRecommendService;

	@PostMapping
	@Secured("ROLE_USER")
	public ResponseEntity<CategoryDto> saveCategory(
		@AuthenticationPrincipal Member member,
		@RequestBody SaveCategoryRequestDto saveCategoryRequestDto
	) {
		CategoryDto categoryDto = categoryService.saveCategory(saveCategoryRequestDto,
			member.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
	}

	@PutMapping("/{categoryId}")
	@Secured("ROLE_USER")
	public ResponseEntity<CategoryDto> updateCategory(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId,
		@RequestBody UpdateCategoryRequestDto updateCategoryRequestDto
	) {
		CategoryDto responseDto = categoryService.updateCategory(updateCategoryRequestDto,
			categoryId, member.getMemberId());
		return ResponseEntity.ok(responseDto);
	}

	@GetMapping("/me")
	@Secured("ROLE_USER")
	public ResponseEntity<CursorPage<CategoryWithHistoryResponseDto>> getCursorPagingCategoryMe(
		@AuthenticationPrincipal Member member,
		@RequestParam(value = "hasNext", required = false) Id hasNext,
		@RequestParam(value = "pageSize") int pageSize,
		@RequestParam(value = "containTitle", required = false) String containTitle
	) {
		CategorySearchOption categorySearchOption = CategorySearchOption.builder()
			.containTitle(containTitle)
			.build();
		CursorPage<CategoryWithHistoryResponseDto> responseDto = categoryService.findCursorPagingCategoryWithHistoryResponse(
			pageSize, member.getMemberId(), hasNext, categorySearchOption);
		return ResponseEntity.ok(responseDto);
	}

	@Secured("ROLE_USER")
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<String> deleteCategory(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId
	) {
		categoryService.deleteCategory(categoryId, member.getMemberId());
		return ResponseEntity.ok().body("");
	}

	@GetMapping("/share")
	public ResponseEntity<CursorPage<SharedCategoryResponseDto>> getCursorPagingCategoryShare(
		@RequestParam(value = "hasNext", required = false) Id hasNext,
		@RequestParam(value = "pageSize") int pageSize,
		@RequestParam(value = "containTitle", required = false) String containTitle
	) {
		CategorySearchOption categorySearchOption = CategorySearchOption.builder()
			.containTitle(containTitle)
			.build();
		CursorPage<SharedCategoryResponseDto> responseDto = categoryService.findCursorPagingCategoryResponseDto(
			pageSize, hasNext, categorySearchOption);
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/{categoryId}/like/like")
	@Secured("ROLE_USER")
	public ResponseEntity<Void> recommendCategory(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId
	) {
		categoryRecommendService.recommend(categoryId, member.getMemberId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{categoryId}/like/unlike")
	@Secured("ROLE_USER")
	public ResponseEntity<Void> cancelCategory(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId
	) {
		categoryRecommendService.cancel(categoryId, member.getMemberId());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/like")
	@Secured("ROLE_USER")
	public ResponseEntity<CategoryRecommendCheckDto> isRecommendCategories(
		@AuthenticationPrincipal Member member,
		@RequestParam(value = "categoryIds") List<Id> categoryIds
	) {
		CategoryRecommendCheckDto recommended = categoryRecommendService.isRecommended(categoryIds,
			member.getMemberId());
		return ResponseEntity.ok(recommended);
	}
}
