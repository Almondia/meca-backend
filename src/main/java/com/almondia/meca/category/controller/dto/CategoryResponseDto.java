package com.almondia.meca.category.controller.dto;

import java.time.LocalDateTime;

import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CategoryResponseDto {

	private final Id categoryId;
	private final Id memberId;
	private final Title title;
	private final boolean isDeleted;
	private final boolean isShared;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
}