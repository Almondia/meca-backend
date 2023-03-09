package com.almondia.meca.category.controller.dto;

import com.almondia.meca.category.domain.vo.Title;

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

	private Title title;
}
