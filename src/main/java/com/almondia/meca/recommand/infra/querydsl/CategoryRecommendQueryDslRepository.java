package com.almondia.meca.recommand.infra.querydsl;

import java.util.List;
import java.util.Map;

import com.almondia.meca.common.domain.vo.Id;

public interface CategoryRecommendQueryDslRepository {

	Map<Id, Long> findRecommendCountByCategoryIds(List<Id> categoryIds);
}
