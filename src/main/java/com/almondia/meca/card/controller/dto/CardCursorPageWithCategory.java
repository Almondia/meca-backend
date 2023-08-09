package com.almondia.meca.card.controller.dto;

import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;

@Getter
public class CardCursorPageWithCategory extends CursorPage<CardWithStatisticsDto> {

	private CategoryDto category;
	private CardMemberDto member;
	private long categoryLikeCount;

	public CardCursorPageWithCategory(CursorPage<CardWithStatisticsDto> cursorPage) {
		super(cursorPage.getContents(), cursorPage.getPageSize(), cursorPage.getHasNext(), cursorPage.getSortOrder());
	}

	public void setMember(Member member) {
		this.member = CardMemberDto.builder()
			.memberId(member.getMemberId())
			.name(member.getName())
			.profile(member.getProfile())
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
