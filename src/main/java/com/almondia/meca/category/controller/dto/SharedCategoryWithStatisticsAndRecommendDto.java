package com.almondia.meca.category.controller.dto;

import com.almondia.meca.member.application.helper.MemberMapper;
import com.almondia.meca.member.controller.dto.MemberDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class SharedCategoryWithStatisticsAndRecommendDto {

	private final CategoryWithStatisticsResponseDto categoryDto;
	private final MemberDto memberDto;

	public SharedCategoryWithStatisticsAndRecommendDto(CategoryWithStatisticsResponseDto categoryDto, Member member) {
		this.categoryDto = categoryDto;
		this.memberDto = MemberMapper.fromEntityToDto(member);
	}
}
