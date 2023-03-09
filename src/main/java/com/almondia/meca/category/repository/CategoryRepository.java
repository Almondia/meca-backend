package com.almondia.meca.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.domain.vo.Id;

public interface CategoryRepository extends JpaRepository<Category, Id> {
}