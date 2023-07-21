package com.almondia.meca.category.infra.querydsl;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.common.domain.vo.Id;
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
	public List<Category> findCategories(int pageSize, Id lastCategoryId, CategorySearchOption categorySearchOption,
		Boolean shared) {
		return jpaQueryFactory.selectFrom(category)
			.where(isShared(shared), category.isDeleted.eq(false), dynamicCursorExpression(lastCategoryId),
				containTitle(categorySearchOption.getContainTitle()))
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();
	}

	@Override
	public List<Category> findCategoriesByMemberId(int pageSize, @Nullable Id lastCategoryId,
		CategorySearchOption categorySearchOption, @Nullable Boolean shared, Id memberId) {
		return jpaQueryFactory.selectFrom(category)
			.where(isShared(shared), category.memberId.eq(memberId), category.isDeleted.eq(false),
				dynamicCursorExpression(lastCategoryId), containTitle(categorySearchOption.getContainTitle()))
			.orderBy(category.categoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();
	}

	@Override
	public List<Category> findSharedCategoriesByRecommend(int pageSize, @Nullable Id lastCategoryId,
		CategorySearchOption categorySearchOption, Id IdWhoRecommend) {
		return jpaQueryFactory.selectDistinct(category)
			.from(category)
			.innerJoin(categoryRecommend)
			.on(category.categoryId.eq(categoryRecommend.categoryId), categoryRecommend.isDeleted.eq(false),
				categoryRecommend.recommendMemberId.eq(IdWhoRecommend))
			.where(category.isShared.eq(true), category.isDeleted.eq(false), dynamicCursorExpression(lastCategoryId),
				containTitle(categorySearchOption.getContainTitle()))
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

	private BooleanExpression dynamicCursorExpression(Id lastCategoryId) {
		return lastCategoryId == null ? null : category.categoryId.uuid.loe(lastCategoryId.getUuid());
	}
}
