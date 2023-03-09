package com.almondia.meca.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.service.CategoryService;
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

	@PutMapping
	@Secured("ROLE_USER")
	public ResponseEntity<CategoryResponseDto> updateCategory(
		@AuthenticationPrincipal Member member,
		@RequestBody UpdateCategoryRequestDto updateCategoryRequestDto
	) {
		CategoryResponseDto responseDto = categoryService.updateCategory(updateCategoryRequestDto,
			member.getMemberId());
		return ResponseEntity.ok(responseDto);
	}
}
