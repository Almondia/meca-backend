package com.almondia.meca.recommand.infra.querydsl;

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
	@DisplayName("findRecommendCategoryIdsByMemberId 테스트")
	class FindRecommendCategoryIdsByMemberIdTest {

		@Test
		@DisplayName("해당 회원이 카테고리 추천이 없는 경우 Id가 비어있어야 한다")
		void shouldReturnFalseWhenMemberHasNoRecommend() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();

			// when
			List<Id> result = categoryRecommendRepository.findRecommendCategoryIdsByMemberId(
				List.of(categoryId), memberId);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("추천이 취소된 경우에 categoryId는 조회되면 안된다")
		void shouldNotReturnIdWhenRecommendIsDeleted() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			CategoryRecommend categoryRecommend = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				memberId);
			categoryRecommend.delete();
			persistAll(categoryRecommend);

			// when
			List<Id> result = categoryRecommendRepository.findRecommendCategoryIdsByMemberId(
				List.of(categoryId), memberId);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("해당 회원에 카테고리 추천이 있는 경우 있는 Id만 반환한다")
		void shouldReturnIdsWhenMemberAndHasRecommend() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			CategoryRecommend categoryRecommend1 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1,
				memberId);
			CategoryRecommend categoryRecommend2 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2,
				memberId);
			persistAll(categoryRecommend1, categoryRecommend2);

			// when
			List<Id> result = categoryRecommendRepository.findRecommendCategoryIdsByMemberId(
				List.of(categoryId1, categoryId2), memberId);

			// then
			assertThat(result).isNotEmpty();
			assertThat(result).containsExactlyInAnyOrder(categoryId1, categoryId2);
		}
	}

	private void persistAll(Object... objects) {
		for (Object object : objects) {
			em.persist(object);
		}
	}
}