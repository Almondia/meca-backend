package com.almondia.meca.category.controller.dto;

import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SharedCategoryResponseDto {

	private final CategoryDto category;
	private final CategoryMemberDto member;
	private long likeCount;

	public SharedCategoryResponseDto(Category category, Member member, long likeCount) {
		this.category = CategoryMapper.entityToCategoryDto(category);
		this.member = toCategoryMemberDto(member);
		this.likeCount = likeCount;
	}

	public SharedCategoryResponseDto(Category category, Member member) {
		this.category = CategoryMapper.entityToCategoryDto(category);
		this.member = toCategoryMemberDto(member);
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	private CategoryMemberDto toCategoryMemberDto(Member member) {
		return CategoryMemberDto.builder()
			.memberId(member.getMemberId())
			.name(member.getName())
			.profile(member.getProfile())
			.build();
	}
}
