package com.almondia.meca.category.controller.dto;

import com.almondia.meca.category.application.helper.CategoryMapper;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.member.application.helper.MemberMapper;
import com.almondia.meca.member.controller.dto.MemberDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class SharedCategoryWithStatisticsAndRecommendDto implements CategoryWithStatisticsDto {

	private final CategoryDto category;
	private final StatisticsDto statistics;
	private final MemberDto member;
	private final long likeCount;

	public SharedCategoryWithStatisticsAndRecommendDto(Category category, Member member, StatisticsDto statisticsDto,
		long likeCount) {
		this.category = CategoryMapper.entityToCategoryDto(category);
		this.member = MemberMapper.fromEntityToDto(member);
		this.statistics = statisticsDto;
		this.likeCount = likeCount;
	}
}
