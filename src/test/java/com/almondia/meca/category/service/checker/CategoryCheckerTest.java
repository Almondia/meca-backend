package com.almondia.meca.category.service.checker;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. 생성된 카테고리가 조건과 일치하면 카테고리 반환
 * 2. 조건과 일치하는 카테고리가 없다면 권한 에러
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CategoryChecker.class)
class CategoryCheckerTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	CategoryChecker categoryChecker;

	@Test
	@DisplayName("생성된 카테고리가 조건과 일치하면 카테고리 반환")
	void shouldReturnCategoryWhenMatchCategoryTest() {
		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		saveCategory(categoryId, memberId);
		Category category = categoryChecker.checkAuthority(categoryId, memberId);
		assertThat(category).isInstanceOf(Category.class);
	}

	@Test
	@DisplayName("조건과 일치하는 카테고리가 없다면 권한 에러")
	void shouldThrowWhenMisMatchCategoryTest() {
		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		saveCategory(categoryId, memberId);
		assertThatThrownBy(() ->
			categoryChecker.checkAuthority(Id.generateNextId(), memberId)).isInstanceOf(AccessDeniedException.class);
	}

	private void saveCategory(Id categoryId, Id memberId) {
		categoryRepository.save(Category.builder()
			.categoryId(categoryId)
			.memberId(memberId)
			.title(new Title("title"))
			.build());
	}

}