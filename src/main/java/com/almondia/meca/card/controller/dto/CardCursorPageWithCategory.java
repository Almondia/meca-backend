package com.almondia.meca.card.controller.dto;

import java.util.List;

import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.controller.dto.MemberDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;

@Getter
public class CardCursorPageWithCategory extends CursorPage<CardWithStatisticsDto> {

	private CategoryDto category;
	private MemberDto member;
	private long categoryLikeCount;

	public CardCursorPageWithCategory(List<CardWithStatisticsDto> contents, Id hasNext, int pageSize,
		SortOrder sortOrder) {
		super(contents, hasNext, pageSize, sortOrder);
	}

	public CardCursorPageWithCategory(CursorPage<CardWithStatisticsDto> cursorPage) {
		super(cursorPage.getContents(), cursorPage.getHasNext(), cursorPage.getPageSize(), cursorPage.getSortOrder());
	}

	public void setMember(Member member) {
		this.member = MemberDto.builder()
			.memberId(member.getMemberId())
			.name(member.getName())
			.email(member.getEmail())
			.profile(member.getProfile())
			.oauthType(member.getOAuthType())
			.role(member.getRole())
			.isDeleted(member.isDeleted())
			.createdAt(member.getCreatedAt())
			.modifiedAt(member.getModifiedAt())
			.build();
	}

	public void setCategory(Category category) {
		this.category = CategoryDto.builder()
			.categoryId(category.getCategoryId())
			.title(category.getTitle())
			.thumbnail(category.getThumbnail())
			.createdAt(category.getCreatedAt())
			.modifiedAt(category.getModifiedAt())
			.memberId(category.getMemberId())
			.isShared(category.isShared())
			.isDeleted(category.isDeleted())
			.build();
	}

	public void setCategoryLikeCount(long categoryLikeCount) {
		this.categoryLikeCount = categoryLikeCount;
	}
}
