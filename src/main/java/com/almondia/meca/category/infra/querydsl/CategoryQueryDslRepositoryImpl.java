package com.almondia.meca.category.infra.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.controller.dto.OffsetPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortFactory;
import com.almondia.meca.common.infra.querydsl.SortField;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryQueryDslRepositoryImpl implements CategoryQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final QCategory category = QCategory.category;
	private final QCardHistory cardHistory = QCardHistory.cardHistory;

	@Override
	public OffsetPage<CategoryResponseDto> findCategories(int offset, int pageSize,
		CategorySearchCriteria categorySearchCriteria, SortOption<? extends SortField> sortOption) {
		List<CategoryResponseDto> categories = jpaQueryFactory.select(
				Projections.constructor(CategoryResponseDto.class, category.categoryId, category.memberId, category.title,
					category.isDeleted, category.isShared, category.createdAt, category.modifiedAt))
			.from(category)
			.where(categorySearchCriteria.getPredicate())
			.orderBy(SortFactory.createOrderSpecifier(sortOption))
			.offset(offset)
			.limit(pageSize)
			.fetch();
		long totalCount = jpaQueryFactory.selectFrom(category).fetch().size();
		return OffsetPage.of(categories, offset, pageSize, (int)totalCount);
	}

	@Override
	public CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId) {
		List<CategoryWithHistoryResponseDto> response = jpaQueryFactory.select(Projections.constructor(
				CategoryWithHistoryResponseDto.class,
				category.categoryId,
				category.memberId,
				category.title,
				category.isDeleted,
				category.isShared,
				category.createdAt,
				category.modifiedAt,
				cardHistory.score.score.avg(),
				cardHistory.score.score.count()
			))
			.from(category)
			.leftJoin(cardHistory)
			.on(category.categoryId.eq(cardHistory.categoryId))
			.where(cursorPagingExpression(memberId, lastCategoryId)
				.and(category.isShared.eq(false)))
			.groupBy(category.categoryId)
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();

		return makeCursorPageWithHistory(pageSize, response);
	}

	@Override
	public CursorPage<CategoryResponseDto> findCategoryShared(int pageSize, Id lastCategoryId) {
		List<CategoryResponseDto> response = jpaQueryFactory.select(
				Projections.constructor(CategoryResponseDto.class,
					category.categoryId,
					category.memberId,
					category.thumbnail,
					category.title,
					category.isDeleted,
					category.isShared,
					category.createdAt,
					category.modifiedAt))
			.from(category)
			.where(category.isShared.eq(true))
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();
		return makeCursorPage(pageSize, response);
	}

	private BooleanExpression cursorPagingExpression(Id memberId, Id lastCategoryId) {
		BooleanExpression loe = null;
		if (lastCategoryId != null) {
			loe = category.categoryId.uuid.loe(lastCategoryId.getUuid());
		}
		return category.memberId.eq(memberId)
			.and(category.isDeleted.eq(false))
			.and(loe);
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

	private CursorPage<CategoryResponseDto> makeCursorPage(int pageSize, List<CategoryResponseDto> response) {
		Id hasNext = null;
		if (response.size() == pageSize + 1) {
			hasNext = response.get(pageSize).getCategoryId();
			response.remove(response.size() - 1);
		}
		return CursorPage.<CategoryResponseDto>builder()
			.contents(response)
			.pageSize(response.size())
			.hasNext(hasNext)
			.sortOrder(SortOrder.DESC)
			.build();
	}
}
