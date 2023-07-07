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

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.helper.recommend.CategoryRecommendTestHelper;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfiguration.class)
class CategoryQueryDslRepositoryImplTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	EntityManager em;

	/**
	 * 카테고리가 없는 경우 contents가 비어있어야 함
	 * 카테고리가 있는 경우 contents가 있어야 함
	 * 카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함
	 * 조회후 다음 페이징 index가 있는 경우 hasNext에 다음 카테고리 id가 존재해야 함
	 * 조회후 다음 페이징 index가 없는 경우 hasNext에 null이 존재해야 함
	 * 조회후 pageSize보다 적은 카테고리를 조회한 경우 hasNext는 null이어야 함
	 * share와 상관 없이 조회할 수 있어야 한다
	 * lastCategoryId를 입력받은 경우, lastCategoryId보다 작은 카테고리는 조회하지 않는다
	 * 풀이한 카드의 경우 풀이한 고유한 카드의 갯수만 조회할 수 있어야 한다
	 * 전체 카드는 풀이한 카드 또는 풀이한 카드와 상관 없이 고유한 카드의 갯수를 조회할 수 있어야 한다
	 * searchOption의 containTitle을 입력받은 경우 해당 문자열을 포함하는 카테고리만 조회할 수 있어야 한다
	 * 카드를 생성후 삭제시 카테고리의 카드 갯수는 0이여야 한다
	 * 추천하지 않은 경우 추천수는 0개여야 한다
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

		@Test
		@DisplayName("share와 상관 없이 조회할 수 있어야 한다")
		void shouldReturnCategoryWhenCategoryIsSharedTest() {
			// given
			Id memberId = Id.generateNextId();
			Id lastCategoryId = Id.generateNextId();
			int pageSize = 3;
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, lastCategoryId));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title2", memberId, Id.generateNextId()));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				lastCategoryId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getContents().get(0).getCategoryId()).isNotNull();
		}

		@Test
		@DisplayName("lastCategoryId를 입력받은 경우, lastCategoryId보다 작은 카테고리는 조회하지 않는다")
		void shouldNotReturnCategoryWhenLastCategoryIdIsNotNullTest() {
			// given
			Id memberId = Id.generateNextId();
			Id lastCategoryId = Id.generateNextId();
			int pageSize = 3;
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, lastCategoryId));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title2", memberId, Id.generateNextId()));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				lastCategoryId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getContents().get(0).getCategoryId()).isEqualTo(lastCategoryId);
		}

		@Test
		@DisplayName("풀이한 카드의 경우 풀이한 고유한 카드의 갯수만 조회할 수 있어야 한다")
		void shouldReturnUniqueCardCountWhenSolvedCardTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 3;
			Id categoryId = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Id cardId3 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId1));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId2));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId3));
			em.persist(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId1, 10));
			em.persist(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId1, 20));
			em.persist(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId2, 10));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents().get(0).getSolveCount()).isEqualTo(2);
		}

		@Test
		@DisplayName("전체 카드는 풀이한 카드 또는 풀이한 카드와 상관 없이 고유한 카드의 갯수를 조회할 수 있어야 한다")
		void shouldReturnUniqueCardCountWhenNotSolvedCardTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 3;
			Id categoryId = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Id cardId3 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId1));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId2));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId3));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents().get(0).getSolveCount()).isEqualTo(0);
			assertThat(result.getContents().get(0).getTotalCount()).isEqualTo(3);
		}

		@Test
		@DisplayName("searchOption의 containTitle을 입력받은 경우 해당 문자열을 포함하는 카테고리만 조회할 수 있어야 한다")
		void shouldReturnCategoryWhenContainTitleTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 3;
			Id categoryId1 = Id.generateNextId();
			Id categoryId2 = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateUnSharedCategory("lts it", memberId, categoryId2));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, cardId1));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId2, cardId2));
			// em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, memberId));
			// em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId2, memberId));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null,
				new CategorySearchOption("it"));

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).hasSize(2);
		}

		@Test
		@DisplayName("카드를 생성후 삭제시 카테고리의 카드 갯수는 0이여야 한다")
		void shouldCardCountIsZeroWhenDeleteCardTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 3;
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()));

			// when
			em.find(Card.class, cardId).delete();
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents().get(0).getTotalCount()).isEqualTo(3L);
		}

		@Test
		@DisplayName("추천하지 않은 경우 추천수는 0개여야 한다")
		void shouldRecommendCountIsZeroWhenNotRecommendTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 3;
			Id categoryId1 = Id.generateNextId();
			Id categoryId2 = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title2", memberId, categoryId2));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, cardId1));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId1, cardId2));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, Id.generateNextId()));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, Id.generateNextId()));
			em.persist(CategoryRecommendTestHelper.generateCategoryRecommend(categoryId1, Id.generateNextId()));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryRepository.findCategoryWithStatisticsByMemberId(
				pageSize,
				memberId,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents().get(0).getLikeCount()).isEqualTo(0L);
			assertThat(result.getContents().get(1).getLikeCount()).isEqualTo(3L);
		}
	}

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

}