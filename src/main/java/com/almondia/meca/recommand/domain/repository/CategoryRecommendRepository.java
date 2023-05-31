package com.almondia.meca.recommand.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;
import com.almondia.meca.recommand.infra.querydsl.CategoryRecommendQueryDslRepository;

public interface CategoryRecommendRepository extends JpaRepository<CategoryRecommend, Id>,
	CategoryRecommendQueryDslRepository {
	Optional<CategoryRecommend> findByCategoryIdAndRecommendMemberId(Id categoryId,
		Id recommendMemberId);

	Optional<CategoryRecommend> findByCategoryIdAndRecommendMemberIdAndIsDeletedFalse(Id categoryId,
		Id recommendMemberId);

	long countByCategoryIdAndIsDeletedFalse(Id categoryId);

}