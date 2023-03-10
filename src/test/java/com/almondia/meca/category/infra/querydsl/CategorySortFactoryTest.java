package com.almondia.meca.category.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.OrderSpecifier;

/**
 * 1. SortFactory가 title, 내림차순을 입력 받았을 때 정상적으로 OrderSpecifier를 생성하는지 검증
 */
class CategorySortFactoryTest {

	@Test
	@DisplayName("SortFactory가 sortOption을 받았을 때 OrderSpecifier를 옳바르게 리턴하는지 검증")
	void shouldReturnRightOrderSpecifier() {
		CategorySortOption sortOption = CategorySortOption.of("title", "desc");
		OrderSpecifier<?> specifier = CategorySortFactory.createOrderSpecifier(sortOption);
		assertThat(specifier.getOrder().name()).isEqualTo("DESC");
		assertThat(specifier.getTarget().toString()).contains("title");
	}
}