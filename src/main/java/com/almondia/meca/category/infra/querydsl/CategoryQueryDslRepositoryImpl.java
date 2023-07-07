package com.almondia.meca.category.infra.querydsl;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.QMember;
import com.almondia.meca.recommand.domain.entity.QCategoryRecommend;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryQueryDslRepositoryImpl implements CategoryQueryDslRepository {

	private static final QCategory category = QCategory.category;
	private static final QCardHistory cardHistory = QCardHistory.cardHistory;
	private static final QCard card = QCard.card;
	private static final QMember member = QMember.member;
	private static final QCategoryRecommend categoryRecommend = QCategoryRecommend.categoryRecommend;

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Category> findCategories(int pageSize, Id lastCategoryId,
		CategorySearchOption categorySearchOption, Boolean shared) {
		return jpaQueryFactory.selectFrom(category)
			.where(
				isShared(shared),
				category.isDeleted.eq(false),
				dynamicCursorExpression(lastCategoryId),
				containTitle(categorySearchOption.getContainTitle())
			)
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();
	}

	@Override
	public List<Category> findCategoriesByMemberId(int pageSize, @Nullable Id lastCategoryId,
		CategorySearchOption categorySearchOption, @Nullable Boolean shared, Id memberId) {
		return jpaQueryFactory.selectFrom(category)
			.where(
				isShared(shared),
				category.memberId.eq(memberId),
				category.isDeleted.eq(false),
				dynamicCursorExpression(lastCategoryId),
				containTitle(categorySearchOption.getContainTitle())
			)
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();
	}

	private BooleanExpression containTitle(String containTitle) {
		return containTitle == null ? null : category.title.title.containsIgnoreCase(containTitle);
	}

	private BooleanExpression isShared(Boolean shared) {
		if (shared == null) {
			return null;
		}
		if (shared) {
			return category.isShared.eq(true);
		}
		return category.isShared.eq(false);
	}

	private BooleanExpression eqMemberId(Id memberId) {
		return memberId == null ? null : category.memberId.eq(memberId);
	}

	private BooleanExpression dynamicCursorExpression(Id lastCategoryId) {
		return lastCategoryId == null ? null : category.categoryId.uuid.loe(lastCategoryId.getUuid());
	}

	private CursorPage<CategoryWithHistoryResponseDto> makeCursorPageWithHistory(int pageSize,
		List<CategoryWithHistoryResponseDto> response) {
		Id hasNext = null;
		if (response.size() == pageSize + 1) {
			hasNext = response.get(pageSize).getCategoryId();
			response.remove(response.size() - 1);
		}
		return CursorPage.<CategoryWithHistoryResponseDto>builder()
			.contents(response)
			.pageSize(response.size())
			.hasNext(hasNext)
			.sortOrder(SortOrder.DESC)
			.build();
	}

	private CursorPage<SharedCategoryResponseDto> makeCursorPage(int pageSize,
		List<SharedCategoryResponseDto> response) {
		Id hasNext = null;
		if (response.size() == pageSize + 1) {
			hasNext = response.get(pageSize).getCategoryInfo().getCategoryId();
			response.remove(response.size() - 1);
		}
		return CursorPage.<SharedCategoryResponseDto>builder()
			.contents(response)
			.pageSize(pageSize)
			.hasNext(hasNext)
			.sortOrder(SortOrder.DESC)
			.build();
	}
}
