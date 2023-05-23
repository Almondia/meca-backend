package com.almondia.meca.recommand.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;

public interface CategoryRecommendRepository extends JpaRepository<CategoryRecommend, Id> {
	Optional<CategoryRecommend> findByCategoryIdAndRecommendMemberId(Id categoryId,
		Id recommendMemberId);

}