package com.almondia.meca.category.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.repository.CategoryRepository;
import com.almondia.meca.category.service.checker.CategoryChecker;
import com.almondia.meca.category.service.helper.CategoryFactory;
import com.almondia.meca.category.service.helper.CategoryMapper;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryChecker categoryChecker;

	@Transactional
	public CategoryResponseDto saveCategory(SaveCategoryRequestDto saveCategoryRequestDto, Id memberId) {
		Category category = CategoryFactory.genCategory(saveCategoryRequestDto, memberId);
		Category result = categoryRepository.save(category);
		return CategoryMapper.entityToCategoryResponseDto(result);
	}

	@Transactional
	public CategoryResponseDto updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto, Id memberId) {
		Category category = categoryChecker.checkAuthority(updateCategoryRequestDto.getCategoryId(), memberId);
		if (updateCategoryRequestDto.getTitle() != null) {
			category.changeTitle(updateCategoryRequestDto.getTitle());
		}
		if (updateCategoryRequestDto.getIsShared() != null) {
			category.changeShare(updateCategoryRequestDto.getIsShared());
		}
		return CategoryMapper.entityToCategoryResponseDto(category);
	}
}