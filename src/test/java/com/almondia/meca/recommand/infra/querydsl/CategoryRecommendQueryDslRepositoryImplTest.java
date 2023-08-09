package com.almondia.meca.recommand.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.recommend.CategoryRecommendTestHelper;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfiguration.class})
class CategoryRecommendQueryDslRepositoryImplTest {

	@Autowired
	EntityManager em;

	@Autowired
	CategoryRecommendRepository categoryRecommendRepository;

	@Nested
	@DisplayName("findRecommendCountByCategoryIds 테스트")
	class FindRecommendCountByCategoryIdsTest {
		@Test
		@DisplayName("해당 카테고리에 추천이 없는 경우 0을 반환한다")
		void shouldReturnZeroWhenCategoryHasNoRecommend() {
			// given
			final Id categoryId = Id.generateNextId();

			// when
			Map<Id, Long> idLongMap = categoryRecommendRepository.findRecommendCountByCategoryIds(
				List.of(categoryId));

			// then
			assertThat(idLongMap).isNotEmpty();
			assertThat(idLongMap.get(categoryId)).isZero();
		}

		@Test
		@DisplayName("해당 카테고리에 추천이 있는 경우 추천 수 만큼 반환한다")
		void shouldReturnCountWhenCategoryHasRecommend() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId1 = Id.generateNextId();
			final Id memberId2 = Id.generateNextId();
			CategoryRecommend categoryRecommend1 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				memberId1);
			CategoryRecommend categoryRecommend2 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				memberId2);
			persistAll(categoryRecommend1, categoryRecommend2);

			// when
			Map<Id, Long> idLongMap = categoryRecommendRepository.findRecommendCountByCategoryIds(
				List.of(categoryId));

			// then
			assertThat(idLongMap)
				.isNotEmpty()
				.containsEntry(categoryId, 2L);
		}

		@Test
		@DisplayName("입력된 카테고리ID 만큼 갯수가 존재해야 한다")
		void shouldReturnCountWhenCategoryHasRecommend2() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id categoryId3 = Id.generateNextId();
			final Id memberId1 = Id.generateNextId();
			final Id memberId2 = Id.generateNextId();
			CategoryRecommend categoryRecommend1 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1,
				memberId1);
			CategoryRecommend categoryRecommend2 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2,
				memberId2);
			persistAll(categoryRecommend1, categoryRecommend2);

			// when
			Map<Id, Long> idLongMap = categoryRecommendRepository.findRecommendCountByCategoryIds(
				List.of(categoryId1, categoryId2, categoryId3));

			// then
			assertThat(idLongMap)
				.containsEntry(categoryId1, 1L)
				.containsEntry(categoryId2, 1L)
				.containsEntry(categoryId3, 0L)
				.hasSize(3);
		}
	}

	private void persistAll(Object... objects) {
		for (Object object : objects) {
			em.persist(object);
		}
	}
}