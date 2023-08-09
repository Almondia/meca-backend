package com.almondia.meca.category.controller.dto;

import com.almondia.meca.category.domain.entity.Category;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CategoryWithStatisticsResponseDto implements CategoryWithStatisticsDto {

	private final CategoryDto category;
	private final CategoryStatisticsDto statistics;
	private long likeCount;

	public CategoryWithStatisticsResponseDto(Category category, CategoryStatisticsDto statistics) {
		this.category = CategoryDto.builder()
			.categoryId(category.getCategoryId())
			.memberId(category.getMemberId())
			.thumbnail(category.getThumbnail())
			.title(category.getTitle())
			.isDeleted(category.isDeleted())
			.isShared(category.isShared())
			.modifiedAt(category.getModifiedAt())
			.build();
		this.statistics = statistics;
	}

	@Builder
	public CategoryWithStatisticsResponseDto(Category category, CategoryStatisticsDto statistics, long likeCount) {
		this.category = CategoryDto.builder()
			.categoryId(category.getCategoryId())
			.memberId(category.getMemberId())
			.thumbnail(category.getThumbnail())
			.title(category.getTitle())
			.isDeleted(category.isDeleted())
			.isShared(category.isShared())
			.modifiedAt(category.getModifiedAt())
			.build();
		this.statistics = statistics;
		this.likeCount = likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}
}
