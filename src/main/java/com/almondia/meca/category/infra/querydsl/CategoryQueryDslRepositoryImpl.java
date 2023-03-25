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
			.where()
			.groupBy(category.categoryId)
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize)
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

	private CursorPage<CategoryWithHistoryResponseDto> makeCursorPage(int pageSize,
		List<CategoryWithHistoryResponseDto> response) {
		Id hasNext = null;
		if (response.size() == pageSize) {
			hasNext = response.get(pageSize - 1).getCategoryId();
		}
		return CursorPage.<CategoryWithHistoryResponseDto>builder()
			.contents(response)
			.pageSize(pageSize)
			.hasNext(hasNext)
			.sortOrder(SortOrder.DESC)
			.build();
	}
}
