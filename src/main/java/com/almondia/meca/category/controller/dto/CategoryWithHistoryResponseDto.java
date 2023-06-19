package com.almondia.meca.category.controller.dto;

import java.time.LocalDateTime;

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
public class CategoryWithHistoryResponseDto {

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

	public CategoryWithHistoryResponseDto(Id categoryId, Id memberId, Image thumbnail, Title title, boolean isDeleted,
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

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}
}
