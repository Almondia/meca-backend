package com.almondia.meca.category.infra.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.common.controller.dto.OffsetPage;
import com.almondia.meca.common.infra.querydsl.SortField;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryQueryDslRepositoryImpl implements CategoryQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final QCategory category = QCategory.category;

	@Override
	public OffsetPage<CategoryResponseDto> findCategories(int offset, int pageSize,
		CategorySearchCriteria categorySearchCriteria, SortOption<? extends SortField> sortOption) {
		List<CategoryResponseDto> categories = jpaQueryFactory.select(
				Projections.constructor(CategoryResponseDto.class, category.categoryId, category.memberId, category.title,
					category.isDeleted, category.isShared, category.createdAt, category.modifiedAt))
			.from(category)
			.where(categorySearchCriteria.getPredicate())
			.orderBy(CategorySortFactory.createOrderSpecifier(sortOption))
			.offset(offset)
			.limit(pageSize)
			.fetch();
		long totalCount = jpaQueryFactory.selectFrom(category).fetch().size();
		return OffsetPage.of(categories, offset, pageSize, (int)totalCount);
	}
}
