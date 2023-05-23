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
		CategoryRecommend categoryRecommend = categoryRecommendRepository.findByCategoryIdAndRecommendMemberId(
				categoryId, memberId)
			.orElseGet(() -> CategoryRecommend.builder()
				.categoryRecommendId(Id.generateNextId())
				.categoryId(categoryId)
				.recommendMemberId(memberId)
				.build());
		if (!categoryRecommend.isDeleted()) {
			throw new IllegalArgumentException("이미 추천한 카테고리입니다");
		}
		categoryRecommend.restore();
	}

	@Transactional
	public void cancel(Id categoryId, Id memberId) {
		CategoryRecommend categoryRecommend =
			categoryRecommendRepository.findByCategoryIdAndRecommendMemberIdAndIsDeletedFalse(categoryId, memberId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 추천을 취소할 수 없습니다"));
		categoryRecommend.delete();
	}

}
