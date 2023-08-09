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
import com.almondia.meca.helper.recommend.CategoryRecommendTestHelper;
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
	 * 카테고리 추천 엔티티가 존재하는 경우 예외 발생
	 * 카테고리 추천을 하지 않은 경우 새로운 정보를 생성에 영속함
	 * 카테고리 추천이 되어있지만 취소된 경우 복구함
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
		@DisplayName("카테고리 추천 엔티티가 존재하는 경우 예외 발생")
		void shouldThrowExceptionWhenCategoryRecommendExist() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			CategoryRecommend categoryRecommend = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				memberId);
			persistAll(member, category, categoryRecommend);

			// expect
			assertThatThrownBy(() -> categoryRecommendService.recommend(categoryId, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("카테고리 추천을 하지 않은 경우 새로운 정보를 생성에 영속함")
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

		@Test
		@DisplayName("카테고리 추천이 되어있지만 취소된 경우 복구함")
		void shouldRecoverWhenRecommendCancel() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			CategoryRecommend categoryRecommend1 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				memberId);
			categoryRecommend1.delete();
			persistAll(member, category, categoryRecommend1);

			// when
			categoryRecommendService.recommend(categoryId, memberId);

			// then
			List<CategoryRecommend> fetch = jpaQueryFactory.selectFrom(categoryRecommend)
				.fetch();
			assertThat(fetch).isNotEmpty();
		}
	}

	/**
	 * 존재하지 않는 카테고리에 등록시 예외 발생
	 * 삭제된 카테고리 추천을 삭제시 예외 발생
	 * 성공적으로 삭제시 삭제 상태로 영속화함
	 */
	@Nested
	@DisplayName("추천 취소")
	class CancelTest {

		@Test
		@DisplayName("존재하지 않는 카테고리에 등록시 예외 발생")
		void shouldThrowExceptionWhenCategoryNotExist() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			persistAll(member);

			// expect
			assertThatThrownBy(() -> categoryRecommendService.cancel(categoryId, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("삭제된 카테고리 추천을 삭제시 예외 발생")
		void shouldThrowExceptionWhenCategoryRecommendDeleted() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			CategoryRecommend categoryRecommend = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				memberId);
			categoryRecommend.delete();
			persistAll(member, category, categoryRecommend);

			// expect
			assertThatThrownBy(() -> categoryRecommendService.cancel(categoryId, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("성공적으로 삭제시 삭제 상태로 영속화함")
		void shouldPersistWhenCancelSuccess() {
			// given
			final Id categoryId = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId);
			CategoryRecommend categoryRecommend1 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				memberId);
			persistAll(member, category, categoryRecommend1);

			// when
			categoryRecommendService.cancel(categoryId, memberId);

			// then
			CategoryRecommend fetch = jpaQueryFactory.selectFrom(categoryRecommend)
				.fetchOne();
			assertThat(fetch).isNotNull();
			assertThat(fetch.isDeleted()).isTrue();
		}
	}

	/**
	 * 추천을 누른 카테고리와 추천을 누르지 않은 카테고리 정보가 정확히 일치해야 함
	 */
	@Nested
	@DisplayName("추천 여부 확인")
	class IsRecommendedTest {

		@Test
		@DisplayName("추천을 누른 카테고리와 추천을 누르지 않은 카테고리 정보가 정확히 일치해야 함")
		void shouldReturnTrueWhenCategoryIsRecommended() {
			// given
			final Id categoryId1 = Id.generateNextId();
			final Id categoryId2 = Id.generateNextId();
			final Id memberId = Id.generateNextId();
			final Id memberId2 = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Member member2 = MemberTestHelper.generateMember(memberId2);
			Category category1 = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId1);
			Category category2 = CategoryTestHelper.generateUnSharedCategory("hello", memberId, categoryId2);
			CategoryRecommend categoryRecommend1 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1,
				memberId);
			CategoryRecommend categoryRecommend2 = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2,
				memberId2);
			persistAll(member, member2, category1, category2, categoryRecommend1, categoryRecommend2);

			// when
			boolean result = categoryRecommendService.isRecommended(
				categoryId1, memberId);

			// then
			assertThat(result).isTrue();
		}

	}
}