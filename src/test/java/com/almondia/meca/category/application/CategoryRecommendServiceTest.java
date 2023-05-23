package com.almondia.meca.category.application;

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
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;
import com.almondia.meca.recommand.domain.entity.QCategoryRecommend;
import com.querydsl.jpa.impl.JPAQueryFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class, CategoryRecommendService.class})
class CategoryRecommendServiceTest {

	private static final QCategoryRecommend categoryRecommend = QCategoryRecommend.categoryRecommend;

	@Autowired
	private EntityManager em;

	@Autowired
	JPAQueryFactory jpaQueryFactory;

	@Autowired
	CategoryRecommendService categoryRecommendService;

	private void persistAll(Object... objects) {
		for (Object object : objects) {
			em.persist(object);
		}
	}

	/**
	 * 존재하지 않는 카테고리에 등록시 예외 발생
	 * 삭제된 카테고리에 등록시 예외 발생
	 * 정상적으로 등록된 경우 영속성 컨텍스트에 저장되어 있음
	 */
	@Nested
	@DisplayName("추천 등록")
	class RecommendTest {

		@Test
		@DisplayName("존재하지 않는 카테고리에 등록시 예외 발생")
		void shouldThrowExceptionWhenCategoryNotExist() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			persistAll(member);

			// expect
			assertThatThrownBy(() -> categoryRecommendService.recommend(categoryId, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("삭제된 카테고리에 등록시 예외 발생")
		void shouldThrowExceptionWhenCategoryDeleted() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			category.delete();
			persistAll(member, category);

			// expect
			assertThatThrownBy(() -> categoryRecommendService.recommend(categoryId, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("정상적으로 등록된 경우 영속성 컨텍스트에 저장되어 있음")
		void shouldPersistWhenRecommendSuccess() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			persistAll(member, category);

			// when
			categoryRecommendService.recommend(categoryId, memberId);

			// then
			List<CategoryRecommend> fetch = jpaQueryFactory.selectFrom(categoryRecommend)
				.fetch();
			assertThat(fetch).isNotEmpty();
		}
	}
}