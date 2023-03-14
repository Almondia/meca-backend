package com.almondia.meca.category.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.OffsetPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;

/**
 * 1. 페이징 형태로 잘 출력이 되는지 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfiguration.class)
class CategoryQueryDslRepositoryImplTest {

	@Autowired
	CategoryRepository categoryRepository;

	@BeforeEach
	void before() {
		inputData();
	}

	@Test
	void test() {
		OffsetPage<CategoryResponseDto> page = categoryRepository.findCategories(
			1,
			2,
			CategorySearchCriteria.builder().build(),
			SortOption.of(CategorySortField.TITLE, SortOrder.ASC));
		assertThat(page).hasFieldOrPropertyWithValue("pageNumber", 0)
			.hasFieldOrPropertyWithValue("totalPages", 1)
			.hasFieldOrPropertyWithValue("pageSize", 2)
			.hasFieldOrPropertyWithValue("totalElements", 4);
	}

	private void inputData() {
		List<Category> categories = List.of(
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.title(new Title("btitle1")).build(),
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.createdAt(LocalDateTime.now().plusHours(4))
				.modifiedAt(LocalDateTime.now().plusHours(4))
				.title(new Title("atitle2")).build(),
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.createdAt(LocalDateTime.now().plusHours(2))
				.modifiedAt(LocalDateTime.now().plusHours(2))
				.title(new Title("ctitle")).build(),
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.createdAt(LocalDateTime.now().plusHours(3))
				.modifiedAt(LocalDateTime.now().plusHours(3))
				.title(new Title("dTitle"))
				.isShared(true).build());

		categoryRepository.saveAllAndFlush(categories);
	}
}