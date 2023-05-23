package com.almondia.meca.category.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryRecommendService {

	private final CategoryRecommendRepository categoryRecommendRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public void recommend(Id categoryId, Id memberId) {
		if (!categoryRepository.existsByCategoryIdAndIsDeletedFalse(categoryId)) {
			throw new IllegalArgumentException("존재하지 않는 카테고리를 추천할 수 없습니다");
		}
		categoryRecommendRepository.save(CategoryRecommend.builder()
			.categoryRecommendId(Id.generateNextId())
			.categoryId(categoryId)
			.recommendMemberId(memberId)
			.build());
	}

}
