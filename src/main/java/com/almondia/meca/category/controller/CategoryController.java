package com.almondia.meca.category.controller;

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

import com.almondia.meca.category.application.CategoryService;
import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	@Secured("ROLE_USER")
	public ResponseEntity<CategoryResponseDto> CategoryEntity(
		@AuthenticationPrincipal Member member,
		@RequestBody SaveCategoryRequestDto saveCategoryRequestDto
	) {
		CategoryResponseDto categoryResponseDto = categoryService.saveCategory(saveCategoryRequestDto,
			member.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
	}

	@PutMapping("/{categoryId}")
	@Secured("ROLE_USER")
	public ResponseEntity<CategoryResponseDto> updateCategory(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId,
		@RequestBody UpdateCategoryRequestDto updateCategoryRequestDto
	) {
		CategoryResponseDto responseDto = categoryService.updateCategory(updateCategoryRequestDto,
			categoryId, member.getMemberId());
		return ResponseEntity.ok(responseDto);
	}

	// @GetMapping("/me")
	// @Secured("ROLE_USER")
	// public ResponseEntity<OffsetPage<CategoryResponseDto>> getOffsetPagingCategory(
	// 	@AuthenticationPrincipal Member member,
	// 	@RequestParam(name = "offset", defaultValue = "0") int offset,
	// 	@RequestParam(name = "pageSize", defaultValue = "1000") int pageSize,
	// 	@RequestParam(name = "sortField", defaultValue = "createdAt") CategorySortField sortField,
	// 	@RequestParam(name = "sortOrder", defaultValue = "desc") SortOrder sortOrder,
	// 	@RequestParam(name = "startTitle", required = false) String startTitle,
	// 	@RequestParam(name = "startCreatedAt", required = false) LocalDateTime startCreatedAt,
	// 	@RequestParam(name = "endCreatedAt", required = false) LocalDateTime endCreatedAt,
	// 	@RequestParam(name = "eqShared", defaultValue = "false") Boolean eqShared
	// ) {
	// 	CategorySearchCriteria categorySearchCriteria = CategorySearchCriteria.builder()
	// 		.startsWithTitle(startTitle)
	// 		.startCreatedAt(startCreatedAt)
	// 		.endCreatedAt(endCreatedAt)
	// 		.eqMemberId(member.getMemberId())
	// 		.eqShared(eqShared)
	// 		.build();
	// 	SortOption<CategorySortField> sortOption = SortOption.of(sortField, sortOrder);
	// 	OffsetPage<CategoryResponseDto> responseDto = categoryService.getOffsetPagingCategoryResponseDto(
	// 		offset, pageSize, categorySearchCriteria, sortOption);
	// 	return ResponseEntity.ok(responseDto);
	// }

	@GetMapping("/me")
	@Secured("ROLE_USER")
	public ResponseEntity<CursorPage<CategoryWithHistoryResponseDto>> getCursorPagingCategory(
		@AuthenticationPrincipal Member member,
		@RequestParam(value = "hasNext", required = false) Id hasNext,
		@RequestParam(value = "pageSize") int pageSize
	) {
		CursorPage<CategoryWithHistoryResponseDto> responseDto = categoryService.findCursorPagingCategoryWithHistoryResponse(
			pageSize, member.getMemberId(), hasNext);
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
}
