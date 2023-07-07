package com.almondia.meca.recommand.infra.querydsl;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.recommand.domain.entity.QCategoryRecommend;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRecommendQueryDslRepositoryImpl implements CategoryRecommendQueryDslRepository {

	private static final QCategoryRecommend categoryRecommend = QCategoryRecommend.categoryRecommend;

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Id> findRecommendCategoryIdsByMemberId(List<Id> categoryIds, Id memberId) {
		return jpaQueryFactory.select(categoryRecommend.categoryId)
			.from(categoryRecommend)
			.where(categoryRecommend.categoryId.in(categoryIds), categoryRecommend.recommendMemberId.eq(memberId),
				categoryRecommend.isDeleted.eq(false))
			.fetch();
	}

	@Override
	public Map<Id, Long> findRecommendCountByCategoryIds(List<Id> categoryIds) {
		Map<Id, Long> collect = jpaQueryFactory.select(categoryRecommend.categoryId,
				new CaseBuilder().when(categoryRecommend.categoryId.count().isNull())
					.then(0L)
					.otherwise(categoryRecommend.categoryId.count()))
			.from(categoryRecommend)
			.where(categoryRecommend.categoryId.in(categoryIds), categoryRecommend.isDeleted.eq(false))
			.groupBy(categoryRecommend.categoryId)
			.fetch()
			.stream()
			.collect(toMap(tuple -> tuple.get(categoryRecommend.categoryId), tuple -> {
				Long aLong = tuple.get(1, Long.class);
				if (aLong == null) {
					return 0L;
				}
				return aLong;
			}));
		for (Id categoryId : categoryIds) {
			if (!collect.containsKey(categoryId)) {
				collect.put(categoryId, 0L);
			}
		}
		return collect;
	}
}
