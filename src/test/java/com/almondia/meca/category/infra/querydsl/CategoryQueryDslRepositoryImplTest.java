package com.almondia.meca.category.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CategoryTestHelper;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfiguration.class)
class CategoryQueryDslRepositoryImplTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	EntityManager em;

	/**
	 * 1. 카테고리가 없는 경우 contents가 비어있어야 함
	 * 2. 카테고리가 있는 경우 contents가 있어야 함
	 * 3. 카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함
	 * 4. 조회후 다음 페이징 index가 있는 경우 hasNext에 다음 카테고리 id가 존재해야 함
	 * 5. 조회후 다음 페이징 index가 없는 경우 hasNext에 null이 존재해야 함
	 * 6. 조회후 pageSize보다 적은 카테고리를 조회한 경우 hasNext는 null이어야 함
	 */
	@Nested
	@DisplayName("findCategoryWithStatisticsByMemberId 메서드 테스트")
	class findCategoryWithStatisticsByMemberIdTest {

		@Test
		@DisplayName("카테고리가 없는 경우 contents가 비어있어야 함")
		void shouldReturnEmptyCursorPageWhenNotExistCategoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id lastCategoryId = Id.generateNextId();
			int pageSize = 10;

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				lastCategoryId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 있는 경우")
		void shouldReturnCursorPageWhenExistCategoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Id lastCategoryId = Id.generateNextId();
			int pageSize = 10;
			em.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, lastCategoryId));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				lastCategoryId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
		}

		@Test
		@DisplayName("카테고리가 있는 경우, pageSize가 0인 경우")
		void shouldReturnCursorPageWhenExistCategoryAndPageSizeIsZeroTest() {
			// given
			Id memberId = Id.generateNextId();
			Id lastCategoryId = Id.generateNextId();
			int pageSize = 0;
			em.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, lastCategoryId));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				lastCategoryId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("조회후 다음 페이징 index가 있는 경우 hasNext에 다음 카테고리 id가 존재해야 함")
		void shouldReturnHasNextWhenExistNextPageTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 1;
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, Id.generateNextId()));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title2", memberId, Id.generateNextId()));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getHasNext()).isNotNull();
		}

		@Test
		@DisplayName("조회후 다음 페이징 index가 없는 경우 hasNext에 null이 존재해야 함")
		void shouldReturnHasNextWhenNotExistNextPageTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 1;
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, Id.generateNextId()));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getHasNext()).isNull();
		}

		@Test
		@DisplayName("조회후 pageSize보다 적은 카테고리를 조회한 경우 hasNext는 null이어야 함")
		void shouldReturnHasNextWhenExistNextPageButPageSizeIsLessThanCategoryCountTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 3;
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, Id.generateNextId()));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title2", memberId, Id.generateNextId()));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getHasNext()).isNull();
		}
	}

}