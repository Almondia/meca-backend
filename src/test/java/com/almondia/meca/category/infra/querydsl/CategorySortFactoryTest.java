package com.almondia.meca.category.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 1. SortFactory가 title, 내림차순을 입력 받았을 때 정상적으로 OrderSpecifier를 생성하는지 검증
 * 2. Factory로 생성한 객체 title 기준 오름차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증
 * 3. Factory로 생성한 객체 title 기준 내림차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증
 * 4. Factory로 생성한 객체 생성일 기준 오름차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증
 * 5. Factory로 생성한 객체 생성일 기준 내림차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증
 * 6. Factory로 생성한 객체 수정일 기준 오름차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증
 * 6. Factory로 생성한 객체 수정일 기중 내림차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfiguration.class)
class CategorySortFactoryTest {

	@Autowired
	JPAQueryFactory queryFactory;

	@Autowired
	CategoryRepository categoryRepository;

	QCategory category = QCategory.category;

	@BeforeEach
	void before() {
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

	@Test
	@DisplayName("SortFactory가 sortOption을 받았을 때 OrderSpecifier를 옳바르게 리턴하는지 검증")
	void shouldReturnRightOrderSpecifier() {
		SortOption<CategorySortField> sortOption = SortOption.of(CategorySortField.TITLE, SortOrder.DESC);
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		assertThat(specifier.getOrder().name()).isEqualTo("DESC");
		assertThat(specifier.getTarget().toString()).contains("title");
	}

	@Test
	@DisplayName("Factory로 생성한 객체 title 기준 오름차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증")
	void orderByTitleAscTest() {
		SortOption<CategorySortField> sortOption = SortOption.of(CategorySortField.TITLE, SortOrder.ASC);
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		List<Category> categories = queryFactory.selectFrom(category)
			.orderBy(specifier)
			.fetch();
		assertThat(categories).extracting(Category::getTitle).isSorted();
	}

	@Test
	@DisplayName("Factory로 생성한 객체 title 기준 내림차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증")
	void orderByTitleDescTest() {
		SortOption<CategorySortField> sortOption = SortOption.of(CategorySortField.TITLE, SortOrder.DESC);
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		List<Category> categories = queryFactory.selectFrom(category)
			.orderBy(specifier)
			.fetch();

		assertThat(categories).extracting(Category::getTitle).isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	@DisplayName("Factory로 생성한 객체 생성일 기준 오름차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증")
	void orderByCreatedAtAscTest() {
		SortOption<CategorySortField> sortOption = SortOption.of(CategorySortField.CREATED_AT, SortOrder.ASC);
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		List<Category> categories = queryFactory.selectFrom(category)
			.orderBy(specifier)
			.fetch();

		assertThat(categories).extracting(Category::getCreatedAt).isSorted();
	}

	@Test
	@DisplayName("Factory로 생성한 객체 생성일 기준 내림차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증")
	void orderByCreatedAtDescTest() {
		SortOption<CategorySortField> sortOption = SortOption.of(CategorySortField.CREATED_AT, SortOrder.DESC);
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		List<Category> categories = queryFactory.selectFrom(category)
			.orderBy(specifier)
			.fetch();

		assertThat(categories).extracting(Category::getCreatedAt).isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	@DisplayName("Factory로 생성한 객체 수정일 기준 오름차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증")
	void orderByModifiedAtAscTest() {
		SortOption<CategorySortField> sortOption = SortOption.of(CategorySortField.MODIFIED_AT, SortOrder.ASC);
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		List<Category> categories = queryFactory.selectFrom(category)
			.orderBy(specifier)
			.fetch();
		assertThat(categories).extracting(Category::getModifiedAt).isSortedAccordingTo(Comparator.naturalOrder());
	}

	@Test
	@DisplayName("Factory로 생성한 객체 수정일 기중 내림차순 정렬이 실제로 쿼리에서 잘 동작하는지 검증")
	void orderByModifiedAtDescTest() {
		SortOption<CategorySortField> sortOption = SortOption.of(CategorySortField.MODIFIED_AT, SortOrder.DESC);
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		List<Category> categories = queryFactory.selectFrom(category)
			.orderBy(specifier)
			.fetch();
		assertThat(categories).extracting(Category::getModifiedAt).isSortedAccordingTo(Comparator.reverseOrder());
	}
}