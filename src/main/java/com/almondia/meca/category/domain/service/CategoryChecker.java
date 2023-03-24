package com.almondia.meca.category.domain.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryChecker {

	private final CategoryRepository categoryRepository;

	public Category checkAuthority(Id categoryId, Id memberId) {
		return categoryRepository.findByCategoryIdAndMemberId(categoryId, memberId)
			.orElseThrow(
				() -> new AccessDeniedException(String.format("%s 사용자가 권한이 없는 카테고리에 접근을 시도했습니다", memberId.toString())));
	}
}
