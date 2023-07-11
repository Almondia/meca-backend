package com.almondia.meca.category.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.helper.recommend.CategoryRecommendTestHelper;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfiguration.class)
class CategoryQueryDslRepositoryImplTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	EntityManager em;

	/**
	 * lastCategoryId를 입력받지 않은 경우, 카테고리는 최신순으로 조회되어야 한다
	 * 넉넉히 존재하는 경우 pageSize보다 하나 더 많이 조회해야 한다
	 * lastCategoryId를 입력받은 경우, lastCategoryId 포함 이전의 카테고리만 조회되어야 한다
	 */
	@Nested
	@DisplayName("findCategories 테스트")
	class FindCategoriesTest {

		@Test
		@DisplayName("lastCategoryId를 입력받지 않은 경우, 카테고리는 최신순으로 조회되어야 한다")
		void shouldReturnCategoryWhenLastCategoryIdIsNullTest() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final int pageSize = 3;
			final boolean shared = true;

			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, categoryId2));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, Id.generateNextId(), Id.generateNextId()));

			// when
			List<Category> result = categoryRepository.findCategories(
				pageSize,
				null
				, CategorySearchOption.builder().build(),
				shared);

			// then
			assertThat(result).isNotEmpty();
			assertThat(result).hasSize(2);
			assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId2);
			assertThat(result.get(0).isShared()).isTrue();
			assertThat(result.get(0).isDeleted()).isFalse();
			assertThat(result.get(1).getCategoryId()).isEqualTo(categoryId1);
			assertThat(result.get(1).isShared()).isTrue();
			assertThat(result.get(1).isDeleted()).isFalse();

		}

		@Test
		@DisplayName("넉넉히 존재하는 경우 pageSize보다 하나 더 많이 조회해야 한다")
		void shouldReturnOneMoreCategoryWhenPageSizeTest() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final int pageSize = 1;
			final boolean shared = true;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, categoryId2));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, Id.generateNextId(), Id.generateNextId()));

			// when
			List<Category> result = categoryRepository.findCategories(
				pageSize,
				null
				, CategorySearchOption.builder().build(),
				shared);

			// then
			assertThat(result).isNotEmpty();
			assertThat(result).hasSize(pageSize + 1);

		}

		@Test
		@DisplayName("lastCategoryId를 입력받은 경우, lastCategoryId 포함 이전의 카테고리만 조회되어야 한다")
		void shouldReturnCategoryWhenLastCategoryIdIsNotNullTest() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id categoryId3 = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final int pageSize = 3;
			final boolean shared = true;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, categoryId2));
			em.persist(CategoryTestHelper.generateSharedCategory("title3", memberId, categoryId3));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId2, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId3, Id.generateNextId()));

			// when
			List<Category> result = categoryRepository.findCategories(
				pageSize,
				categoryId2,
				CategorySearchOption.builder().build(),
				shared);

			// then
			assertThat(result).isNotEmpty();
			assertThat(result).hasSize(2);
			assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId2);
			assertThat(result.get(0).isShared()).isEqualTo(true);
			assertThat(result.get(0).isDeleted()).isFalse();
			assertThat(result.get(1).getCategoryId()).isEqualTo(categoryId1);
			assertThat(result.get(1).isShared()).isEqualTo(true);
			assertThat(result.get(1).isDeleted()).isFalse();
		}

		@Test
		@DisplayName("containTitle 카테고리 옵션을 이용하는 경우 like를 이용해 카테고리를 필터링 검색한다")
		void shouldReturnFilteringCategoryWhenUsingContainTitleOptionTest() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id categoryId3 = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final int pageSize = 3;
			final boolean shared = true;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, categoryId2));
			em.persist(CategoryTestHelper.generateSharedCategory("titlz3", memberId, categoryId3));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId2, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId3, Id.generateNextId()));

			// when
			List<Category> result = categoryRepository.findCategories(
				pageSize,
				null,
				CategorySearchOption.builder().containTitle("title").build(),
				shared);

			// then
			assertThat(result).isNotEmpty();
			assertThat(result).hasSize(2);
		}

		@Test
		@DisplayName("shared가 false인 경우 공유되지 않은 카테고리만 조회한다")
		void shouldSearchUnSharedCategoryWhenSharedFalseTest() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id categoryId3 = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final int pageSize = 3;
			final boolean shared = false;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, categoryId2));
			em.persist(CategoryTestHelper.generateSharedCategory("titlz3", memberId, categoryId3));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId2, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId3, Id.generateNextId()));

			// when
			List<Category> result = categoryRepository.findCategories(
				pageSize,
				null,
				CategorySearchOption.builder().build(),
				shared);

			// then
			assertThat(result).isNotEmpty();
			assertThat(result).hasSize(1);
		}
	}

	@Test
	@DisplayName("shared가 null인 경우 공유 여부 상관 없이 조회한다")
	void shouldReturnAllKindsOfSharedCategoryWhenSharedNullTest() {
		// given
		final Id categoryId1 = Id.generateNextId();
		final Id categoryId2 = Id.generateNextId();
		final Id categoryId3 = Id.generateNextId();
		final Id memberId = Id.generateNextId();
		final int pageSize = 3;
		em.persist(MemberTestHelper.generateMember(memberId));
		em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId1));
		em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, categoryId2));
		em.persist(CategoryTestHelper.generateSharedCategory("titlz3", memberId, categoryId3));
		em.persist(CardTestHelper.genOxCard(memberId, categoryId1, Id.generateNextId()));
		em.persist(CardTestHelper.genOxCard(memberId, categoryId2, Id.generateNextId()));
		em.persist(CardTestHelper.genOxCard(memberId, categoryId3, Id.generateNextId()));

		// when
		List<Category> result = categoryRepository.findCategories(
			pageSize,
			null,
			CategorySearchOption.builder().build(),
			null);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(3);
	}

	@Nested
	@DisplayName("findCategoriesByMemberId 메서드 테스트")
	class FindCategoriesByMemberIdTest {

		@Test
		@DisplayName("특정 회원 유저의 카테고리를 조회한다")
		void shouldReturnCategoryByMemberIdTest() {
			// given
			final Id memberId1 = Id.generateNextId();
			final Id memberId2 = Id.generateNextId();
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id categoryId3 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId1, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId1, categoryId2));
			em.persist(CategoryTestHelper.generateSharedCategory("titlz3", memberId2, categoryId3));

			// when
			List<Category> result = categoryRepository.findCategoriesByMemberId(10, null,
				CategorySearchOption.builder().build(), null, memberId1);

			// then
			assertThat(result).hasSize(2);
		}
	}

	/**
	 * 공유된 카테고리만을 조회해야 한다
	 * 추천한 사람의 카테고리를 조회해야 한다
	 * 삭제된 추천의 카테고리를 조회하면 안된다
	 * 삭제된 카테고리를 조회하면 안된다
	 * lastCateogy를 입력하면 그 정보를 포함 및 이전에 생성된 카테고리를 조회한다
	 */
	@Nested
	@DisplayName("findSharedCategoriesByRecommend")
	class FindSharedCategoriesByRecommendTest {

		@Test
		@DisplayName("공유된 카테고리만을 조회해야 한다")
		void shouldReturnSharedCategoryTest() {
			// given
			final Id memberId1 = Id.generateNextId();
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId1, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId1, categoryId2));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId1));

			// when
			List<Category> result = categoryRepository.findSharedCategoriesByRecommend(10, null,
				CategorySearchOption.builder().build(), memberId1);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId2);
		}

		@Test
		@DisplayName("추천한 사람의 카테고리를 조회해야 한다")
		void shouldReturnCategoryByRecommendMemberTest() {
			// given
			final Id memberId1 = Id.generateNextId();
			final Id memberId2 = Id.generateNextId();
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId1, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId1, categoryId2));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId2));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId2));

			// when
			List<Category> result = categoryRepository.findSharedCategoriesByRecommend(10, null,
				CategorySearchOption.builder().build(), memberId2);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId2);
		}

		@Test
		@DisplayName("삭제된 추천의 카테고리를 조회하면 안된다")
		void shouldNotReturnCategoryByDeletedRecommendTest() {
			// given
			final Id memberId1 = Id.generateNextId();
			final Id memberId2 = Id.generateNextId();
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId1, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId1, categoryId2));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, memberId1));
			CategoryRecommend categoryRecommend = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2,
				memberId2);
			categoryRecommend.delete();
			em.persist(categoryRecommend);

			// when
			List<Category> result = categoryRepository.findSharedCategoriesByRecommend(10, null,
				CategorySearchOption.builder().build(), memberId2);

			// then
			assertThat(result).hasSize(0);
		}

		@Test
		@DisplayName("삭제된 카테고리를 조회하면 안된다")
		void shouldNotReturnCategoryIsDeletedTrueTest() {
			// given
			final Id memberId1 = Id.generateNextId();
			final Id memberId2 = Id.generateNextId();
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId1, categoryId1));
			Category category = CategoryTestHelper.generateSharedCategory("title2", memberId1, categoryId2);
			category.delete();
			em.persist(category);
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId2));

			// when
			List<Category> result = categoryRepository.findSharedCategoriesByRecommend(10, null,
				CategorySearchOption.builder().build(), memberId2);

			// then
			assertThat(result).hasSize(0);
		}

		@Test
		@DisplayName("lastCateogy를 입력하면 그 정보를 포함 및 이전에 생성된 카테고리를 조회한다")
		void shouldReturnCategoryBeforeCreatedWhenInputLastCategoryIdTest() {
			// given
			final Id memberId1 = Id.generateNextId();
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id categoryId3 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId1, categoryId1));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId1, categoryId2));
			em.persist(CategoryTestHelper.generateSharedCategory("title3", memberId1, categoryId3));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId1));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId3, memberId1));

			// when
			List<Category> result = categoryRepository.findSharedCategoriesByRecommend(10, categoryId2,
				CategorySearchOption.builder().build(), memberId1);

			// then
			assertThat(result).hasSize(2);
			assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId2);
			assertThat(result.get(1).getCategoryId()).isEqualTo(categoryId1);
		}
	}

}