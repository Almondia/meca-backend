package com.almondia.meca.category.infra.querydsl;

import java.util.List;

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
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
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
	public CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId) {
		SubQueryExpression<Long> subQuery = jpaQueryFactory.select(categoryRecommend.count())
			.from(categoryRecommend)
			.where(categoryRecommend.categoryId.eq(category.categoryId),
				categoryRecommend.isDeleted.eq(false));

		List<CategoryWithHistoryResponseDto> response = jpaQueryFactory.select(Projections.constructor(
				CategoryWithHistoryResponseDto.class,
				category.categoryId,
				category.memberId,
				category.thumbnail,
				category.title,
				category.isDeleted,
				category.isShared,
				category.createdAt,
				category.modifiedAt,
				cardHistory.score.score.avg(),
				cardHistory.cardId.countDistinct(),
				card.cardId.countDistinct(),
				subQuery
			))
			.from(category)
			.leftJoin(card)
			.on(
				category.categoryId.eq(card.categoryId),
				card.isDeleted.eq(false))
			.leftJoin(cardHistory)
			.on(
				card.cardId.eq(cardHistory.cardId),
				cardHistory.isDeleted.eq(false)
			)
			.where(
				category.isDeleted.eq(false),
				eqMemberId(memberId),
				dynamicCursorExpression(lastCategoryId))
			.groupBy(category.categoryId)
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();

		return makeCursorPageWithHistory(pageSize, response);
	}

	@Override
	public CursorPage<CategoryWithHistoryResponseDto> findCategoryWithStatisticsByMemberId(int pageSize, Id memberId,
		Id lastCategoryId, CategorySearchOption categorySearchOption
	) {
		SubQueryExpression<Long> subQuery = jpaQueryFactory.select(categoryRecommend.count())
			.from(categoryRecommend)
			.where(categoryRecommend.categoryId.eq(category.categoryId),
				categoryRecommend.isDeleted.eq(false));

		List<CategoryWithHistoryResponseDto> response = jpaQueryFactory.select(Projections.constructor(
				CategoryWithHistoryResponseDto.class,
				category.categoryId,
				category.memberId,
				category.thumbnail,
				category.title,
				category.isDeleted,
				category.isShared,
				category.createdAt,
				category.modifiedAt,
				cardHistory.score.score.avg(),
				cardHistory.cardId.countDistinct(),
				card.cardId.countDistinct(),
				subQuery
			))
			.from(category)
			.leftJoin(card)
			.on(
				category.categoryId.eq(card.categoryId),
				card.isDeleted.eq(false))
			.leftJoin(cardHistory)
			.on(
				card.cardId.eq(cardHistory.cardId),
				cardHistory.isDeleted.eq(false)
			)
			.where(
				category.isDeleted.eq(false),
				eqMemberId(memberId),
				dynamicCursorExpression(lastCategoryId),
				containTitle(categorySearchOption.getContainTitle()))
			.groupBy(category.categoryId)
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();

		return makeCursorPageWithHistory(pageSize, response);
	}

	@Override
	public CursorPage<SharedCategoryResponseDto> findCategoryShared(int pageSize, Id lastCategoryId) {
		SubQueryExpression<Long> subQuery = jpaQueryFactory.select(categoryRecommend.count())
			.from(categoryRecommend)
			.where(categoryRecommend.categoryId.eq(category.categoryId),
				categoryRecommend.isDeleted.eq(false));

		List<SharedCategoryResponseDto> response = jpaQueryFactory.select(
				Projections.constructor(SharedCategoryResponseDto.class,
					category,
					member,
					subQuery)
			)
			.from(category)
			.where(
				category.isShared.eq(true),
				dynamicCursorExpression(lastCategoryId)
			)
			.innerJoin(member)
			.on(category.memberId.eq(member.memberId),
				member.isDeleted.eq(false)
			)
			.leftJoin(card)
			.on(category.categoryId.eq(card.categoryId),
				card.isDeleted.eq(false)
			)
			.groupBy(category.categoryId)
			.having(card.cardId.countDistinct().gt(0))
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();
		return makeCursorPage(pageSize, response);
	}

	@Override
	public CursorPage<SharedCategoryResponseDto> findCategoryShared(int pageSize, Id lastCategoryId,
		CategorySearchOption categorySearchOption) {
		SubQueryExpression<Long> subQuery = jpaQueryFactory.select(categoryRecommend.count())
			.from(categoryRecommend)
			.where(categoryRecommend.categoryId.eq(category.categoryId),
				categoryRecommend.isDeleted.eq(false));

		List<SharedCategoryResponseDto> response = jpaQueryFactory.select(
				Projections.constructor(SharedCategoryResponseDto.class,
					category,
					member,
					subQuery
				)
			)
			.from(category)
			.innerJoin(member)
			.on(
				category.memberId.eq(member.memberId),
				member.isDeleted.eq(false)
			)
			.leftJoin(card)
			.on(
				category.categoryId.eq(card.categoryId),
				card.isDeleted.eq(false)
			)
			.groupBy(category.categoryId)
			.having(card.cardId.countDistinct().gt(0))
			.where(
				category.isShared.eq(true),
				dynamicCursorExpression(lastCategoryId),
				containTitle(categorySearchOption.getContainTitle()))
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();
		return makeCursorPage(pageSize, response);
	}

	@Override
	public List<Category> findSharedCategories(int pageSize, Id lastCategoryId,
		CategorySearchOption categorySearchOption) {
		return jpaQueryFactory.selectFrom(category)
			.where(
				category.isShared.eq(true),
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
