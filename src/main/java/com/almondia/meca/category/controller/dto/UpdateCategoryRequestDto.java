package com.almondia.meca.category.controller.dto;

import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateCategoryRequestDto {

	private Id categoryId;
	private Title title;
	private Boolean isShared;
}
