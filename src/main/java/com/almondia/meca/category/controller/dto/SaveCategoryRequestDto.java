package com.almondia.meca.category.controller.dto;

import org.springframework.lang.Nullable;

import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Image;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SaveCategoryRequestDto {

	@Nullable
	private Image thumbnail;
	private Title title;
}
