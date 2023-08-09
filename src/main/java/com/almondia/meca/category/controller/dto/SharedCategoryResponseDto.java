package com.almondia.meca.category.controller.dto;

import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.member.application.helper.MemberMapper;
import com.almondia.meca.member.controller.dto.MemberDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SharedCategoryResponseDto {

	private final CategoryDto category;
	private final MemberDto member;
	private long likeCount;

	public SharedCategoryResponseDto(Category category, Member member, long likeCount) {
		this.category = CategoryMapper.entityToCategoryDto(category);
		this.member = MemberMapper.fromEntityToDto(member);
		this.likeCount = likeCount;
	}

	public SharedCategoryResponseDto(Category category, Member member) {
		this.category = CategoryMapper.entityToCategoryDto(category);
		this.member = MemberMapper.fromEntityToDto(member);
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}
}
