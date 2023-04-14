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
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;

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
	 * 7. share와 상관 없이 조회할 수 있어야 한다
	 * 8. lastCategoryId를 입력받은 경우, lastCategoryId보다 작은 카테고리는 조회하지 않는다
	 * 9. 풀이한 카드의 경우 풀이한 고유한 카드의 갯수만 조회할 수 있어야 한다
	 * 10. 전체 카드는 풀이한 카드 또는 풀이한 카드와 상관 없이 고유한 카드의 갯수를 조회할 수 있어야 한다
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
			em.persist(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId1, categoryId, 10));
			em.persist(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId1, categoryId, 20));
			em.persist(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), cardId2, categoryId, 10));

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
	}

	/**
	 * 1. 카테고리가 없는 경우 contents가 비어있어야 함
	 * 2. 카테고리가 있는 경우 contents가 있어야 함
	 * 3. 카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함
	 * 4. 조회후 다음 페이징 index가 있는 경우 hasNext에 다음 카테고리 id가 존재해야 함
	 * 5. 조회후 다음 페이징 index가 없는 경우 hasNext에 null이 존재해야 함
	 * 6. shared가 false인 카테고리는 조회하면 안된다
	 * 7. "lastCategoryId를 입력받은 경우, lastCategoryId와 같거나 큰 카테고리는 조회되어야 한다"
	 */
	@Nested
	@DisplayName("findCategoryShared 테스트")
	class FindCategorySharedTest {

		Id memberId = Id.generateNextId();

		@Test
		@DisplayName("카테고리가 없는 경우 contents가 비어있어야 함")
		void shouldReturnEmptyContentsWhenNotExistCategoryTest() {
			// given
			int pageSize = 1;

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryRepository.findCategoryShared(
				pageSize,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 있는 경우 contents가 있어야 함")
		void shouldReturnContentsWhenExistCategoryTest() {
			// given
			int pageSize = 1;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, Id.generateNextId()));

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryRepository.findCategoryShared(
				pageSize,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getContents().get(0))
				.hasFieldOrProperty("category")
				.hasFieldOrProperty("member");
		}

		@Test
		@DisplayName("카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함")
		void shouldReturnEmptyContentsWhenExistCategoryAndPageSizeIsZeroTest() {
			// given
			int pageSize = 0;
			em.persist(CategoryTestHelper.generateSharedCategory("title1", Id.generateNextId(), Id.generateNextId()));

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryRepository.findCategoryShared(
				pageSize,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("조회후 다음 페이징 index가 있는 경우 hasNext에 다음 카테고리 id가 존재해야 함")
		void shouldReturnHasNextWhenExistNextPageTest() {
			// given
			int pageSize = 1;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, Id.generateNextId()));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, Id.generateNextId()));

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryRepository.findCategoryShared(
				pageSize,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getContents().get(0))
				.hasFieldOrProperty("category")
				.hasFieldOrProperty("member");
			assertThat(result.getHasNext()).isNotNull();
		}

		@Test
		@DisplayName("조회후 다음 페이징 index가 없는 경우 hasNext에 null이 존재해야 함")
		void shouldReturnHasNextWhenExistNextPageButPageSizeIsLessThanCategoryCountTest() {
			// given
			int pageSize = 3;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, Id.generateNextId()));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, Id.generateNextId()));

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryRepository.findCategoryShared(
				pageSize,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getContents().get(0))
				.hasFieldOrProperty("category")
				.hasFieldOrProperty("member");
			assertThat(result.getHasNext()).isNull();
		}

		@Test
		@DisplayName("share가 false인 카테고리는 조회하면 안된다")
		void shouldNotReturnCategoryWhenCategoryIsNotSharedTest() {
			// given
			Id memberId = Id.generateNextId();
			int pageSize = 3;
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, Id.generateNextId()));

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryRepository.findCategoryShared(
				pageSize,
				null);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("lastCategoryId를 입력받은 경우, lastCategoryId와 같거나 큰 카테고리는 조회되어야 한다")
		void shouldReturnCategoryWhenLastCategoryIdIsNotNullTest() {
			// given
			Id lastCategoryId = Id.generateNextId();
			int pageSize = 3;
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateSharedCategory("title1", memberId, lastCategoryId));
			em.persist(CategoryTestHelper.generateSharedCategory("title2", memberId, Id.generateNextId()));

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryRepository.findCategoryShared(
				pageSize,
				lastCategoryId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getContents()).isNotEmpty();
			assertThat(result.getContents().get(0))
				.hasFieldOrProperty("category")
				.hasFieldOrProperty("member");
			assertThat(result.getContents().get(0).getCategory().getCategoryId()).isEqualTo(lastCategoryId);
		}
	}

}