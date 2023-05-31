package com.almondia.meca.recommand.infra.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.recommand.domain.entity.QCategoryRecommend;
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
			.where(
				categoryRecommend.categoryId.in(categoryIds),
				categoryRecommend.recommendMemberId.eq(memberId),
				categoryRecommend.isDeleted.eq(false)
			).fetch();
	}
}
