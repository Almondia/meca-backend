package com.almondia.meca.category.controller.dto;

import java.time.LocalDateTime;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class CategoryWithStatisticsResponseDto implements CategoryWithStatisticsDto {

	private final Id categoryId;
	private final Id memberId;
	private final Image thumbnail;
	private final Title title;
	private final boolean isDeleted;
	private final boolean isShared;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
	private final double scoreAvg;
	private final long solveCount;
	private final long totalCount;
	private long likeCount;

	public CategoryWithStatisticsResponseDto(Id categoryId, Id memberId, Image thumbnail, Title title,
		boolean isDeleted,
		boolean isShared, LocalDateTime createdAt, LocalDateTime modifiedAt, double scoreAvg, long solveCount,
		long totalCount) {
		this.categoryId = categoryId;
		this.memberId = memberId;
		this.thumbnail = thumbnail;
		this.title = title;
		this.isDeleted = isDeleted;
		this.isShared = isShared;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.scoreAvg = scoreAvg;
		this.solveCount = solveCount;
		this.totalCount = totalCount;
	}

	public CategoryWithStatisticsResponseDto(Category category, double scoreAvg, long solveCount, long totalCount,
		long likeCount) {
		this.categoryId = category.getCategoryId();
		this.memberId = category.getMemberId();
		this.thumbnail = category.getThumbnail();
		this.title = category.getTitle();
		this.isDeleted = category.isDeleted();
		this.isShared = category.isShared();
		this.createdAt = category.getCreatedAt();
		this.modifiedAt = category.getModifiedAt();
		this.scoreAvg = scoreAvg;
		this.solveCount = solveCount;
		this.totalCount = totalCount;
		this.likeCount = likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}
}
