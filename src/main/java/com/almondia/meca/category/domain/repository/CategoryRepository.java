package com.almondia.meca.category.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.infra.querydsl.CategoryQueryDslRepository;
import com.almondia.meca.common.domain.vo.Id;

public interface CategoryRepository extends JpaRepository<Category, Id>, CategoryQueryDslRepository {
	Optional<Category> findByCategoryIdAndMemberId(Id categoryId, Id memberId);

	Optional<Category> findByCategoryIdAndIsDeleted(Id categoryId, boolean isDeleted);

	Optional<Category> findByCategoryIdAndIsDeletedFalse(Id categoryId);
}