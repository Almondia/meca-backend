package com.almondia.meca.helper.recommend;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;

public class CategoryRecommendTestHelper {

	public static CategoryRecommend generateCategoryRecommend(Id categoryId, Id recommendMemberId) {
		return CategoryRecommend.builder()
			.categoryRecommendId(Id.generateNextId())
			.categoryId(categoryId)
			.recommendMemberId(recommendMemberId)
			.build();
	}
}
