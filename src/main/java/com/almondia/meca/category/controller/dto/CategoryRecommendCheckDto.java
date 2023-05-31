package com.almondia.meca.category.controller.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.almondia.meca.common.domain.vo.Id;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CategoryRecommendCheckDto {

	Set<Id> recommendedCategories;
	Set<Id> unRecommendedCategories;

	public CategoryRecommendCheckDto(List<Id> inputCategoryIds, List<Id> recommendedCategoryIds) {
		this.recommendedCategories = Set.copyOf(recommendedCategoryIds);
		this.unRecommendedCategories = inputCategoryIds.stream()
			.filter(categoryId -> !recommendedCategoryIds.contains(categoryId))
			.collect(Collectors.toSet());
	}
}
