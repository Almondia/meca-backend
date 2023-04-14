package com.almondia.meca.category.controller.dto;

import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.member.application.helper.MemberMapper;
import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;

@Getter
public class SharedCategoryResponseDto {

	private final CategoryResponseDto category;
	private final MemberResponseDto member;

	public SharedCategoryResponseDto(Category category, Member member) {
		this.category = CategoryMapper.entityToCategoryResponseDto(category);
		this.member = MemberMapper.fromEntityToDto(member);
	}
}
