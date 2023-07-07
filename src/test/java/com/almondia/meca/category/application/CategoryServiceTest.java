package com.almondia.meca.category.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.category.infra.querydsl.CategorySearchOption;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.repository.MemberRepository;

class CategoryServiceTest {

	/**
	 * 1. 카테고리 등록시 영속성 테스트
	 * 2. 카테고리 thubnail 이미지가 없어도 등록 가능 여부 테스트
	 * 3. title이 null이면 등록 불가능 여부 테스트
	 */
	@Nested
	@DisplayName("카테고리 등록")
	@DataJpaTest
	@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	@Import({CategoryService.class, QueryDslConfiguration.class})
	class SaveCategoryTest {

		@Autowired
		CategoryRepository categoryRepository;

		@MockBean
		CategoryChecker categoryChecker;

		@MockBean
		CardRepository cardRepository;

		@MockBean
		CardHistoryRepository cardHistoryRepository;

		@Autowired
		CategoryService categoryService;

		@Test
		@DisplayName("카테고리 등록시 영속성 테스트")
		void saveCategoryTest() {
			// given
			SaveCategoryRequestDto saveCategoryRequestDto = SaveCategoryRequestDto.builder()
				.title(Title.of("title"))
				.thumbnail(Image.of("thumbnail"))
				.build();

			// when
			CategoryDto result = categoryService.saveCategory(saveCategoryRequestDto,
				Id.generateNextId());

			// then
			Category category = categoryRepository.findAll().get(0);
			assertThat(category.getTitle()).isEqualTo(Title.of("title"));
			assertThat(category.getThumbnail()).isEqualTo(Image.of("thumbnail"));
			assertThat(result).extracting("title", "thumbnail")
				.containsExactly(Title.of("title"), Image.of("thumbnail"));
		}

		@Test
		@DisplayName("카테고리 thubnail 이미지가 없어도 등록 가능 여부 테스트")
		void saveCategoryWithoutThumbnailTest() {
			// given
			SaveCategoryRequestDto saveCategoryRequestDto = SaveCategoryRequestDto.builder()
				.title(Title.of("title"))
				.build();

			// when
			CategoryDto result = categoryService.saveCategory(saveCategoryRequestDto,
				Id.generateNextId());

			// then
			Category category = categoryRepository.findAll().get(0);
			assertThat(category.getTitle()).isEqualTo(Title.of("title"));
			assertThat(category.getThumbnail()).isNull();
			assertThat(result)
				.extracting("title", "thumbnail")
				.containsExactly(Title.of("title"), null);
		}

		@Test
		@DisplayName("title이 null이면 등록 불가능 여부 테스트")
		void saveCategoryWithNullTitleTest() {
			// given
			SaveCategoryRequestDto saveCategoryRequestDto = SaveCategoryRequestDto.builder()
				.thumbnail(Image.of("thumbnail"))
				.build();

			// when
			assertThatThrownBy(() -> categoryService.saveCategory(saveCategoryRequestDto,
				Id.generateNextId()))
				.isInstanceOf(DataIntegrityViolationException.class);
		}
	}

	/**
	 * 1. 사용자가 본인 권한 외에 카테고리를 호출하면 예외 발생 여부 테스트
	 * 2. 수정 요청에  title만 입력된 경우 title만 수정되는지 테스트
	 * 3. 수정 요청에  thumbnail만 입력된 경우 thumbnail만 수정되는지 테스트
	 * 4. 수정 요청에  isShared만 입력된 경우 isShared만 수정되는지 테스트
	 * 5. 수정 요청에  title, thumbnail, isShared 모두 입력된 경우 title, thumbnail, isShared 모두 수정되는지 테스트
	 */
	@Nested
	@DisplayName("카테고리 수정")
	@DataJpaTest
	@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	@Import({CategoryService.class, QueryDslConfiguration.class})
	class UpdateCategoryTest {

		@Autowired
		CategoryRepository categoryRepository;

		@MockBean
		CategoryChecker categoryChecker;

		@MockBean
		CardRepository cardRepository;

		@MockBean
		CardHistoryRepository cardHistoryRepository;

		@Autowired
		CategoryService categoryService;

		@Autowired
		EntityManager entityManager;

		@Test
		@DisplayName("사용자가 본인 권한 외에 카테고리를 호출하면 예외 발생 여부 테스트")
		void updateCategoryWithNotOwnerTest() {
			// given
			Mockito.doThrow(AccessDeniedException.class)
				.when(categoryChecker)
				.checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(Title.of("title"))
				.thumbnail(Image.of("thumbnail"))
				.shared(true)
				.build();

			// when
			assertThatThrownBy(() -> categoryService.updateCategory(updateCategoryRequestDto, Id.generateNextId(),
				Id.generateNextId()))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("수정 요청에 title만 입력된 경우 title만 수정되는지 테스트")
		void updateCategoryWithOnlyTitleTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(Title.of("update title"))
				.build();

			// when
			CategoryDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("update title"));
			assertThat(updatedCategory.isShared()).isFalse();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("update title"), null, false);
		}

		@Test
		@DisplayName("수정 요청에 thumbnail만 입력된 경우 thumbnail만 수정되는지 테스트")
		void updateCategoryWithOnlyThumbnailTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.thumbnail(Image.of("update thumbnail"))
				.build();

			// when
			CategoryDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("title"));
			assertThat(updatedCategory.getThumbnail()).isEqualTo(Image.of("update thumbnail"));
			assertThat(updatedCategory.isShared()).isFalse();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("title"), Image.of("update thumbnail"), false);
		}

		@Test
		@DisplayName("수정 요청에 isShared만 입력된 경우 isShared만 수정되는지 테스트")
		void updateCategoryWithOnlyIsSharedTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.shared(true)
				.build();

			// when
			CategoryDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("title"));
			assertThat(updatedCategory.getThumbnail()).isNull();
			assertThat(updatedCategory.isShared()).isTrue();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("title"), null, true);
		}

		@Test
		@DisplayName("수정 요청에 title, thumbnail, isShared 모두 입력된 경우 title, thumbnail, isShared 모두 수정되는지 테스트")
		void updateCategoryWithAllTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(Title.of("update title"))
				.thumbnail(Image.of("update thumbnail"))
				.shared(true)
				.build();

			// when
			CategoryDto result = categoryService.updateCategory(updateCategoryRequestDto,
				category.getCategoryId(),
				category.getMemberId());

			// then
			Category updatedCategory = categoryRepository.findAll().get(0);
			assertThat(updatedCategory.getTitle()).isEqualTo(Title.of("update title"));
			assertThat(updatedCategory.getThumbnail()).isEqualTo(Image.of("update thumbnail"));
			assertThat(updatedCategory.isShared()).isTrue();
			assertThat(result)
				.extracting("title", "thumbnail", "isShared")
				.containsExactly(Title.of("update title"), Image.of("update thumbnail"), true);
		}
	}

	/**
	 * 1. 카테고리 삭제 테스트
	 * 2. 삭제 요청한 카테고리가 존재하지 않는 경우 예외가 발생하는지 테스트
	 * 3. 삭제 요청한 카테고리의 소유자가 아닌 경우 예외가 발생하는지 테스트
	 */
	@Nested
	@DisplayName("카테고리 삭제 테스트")
	@DataJpaTest
	@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	@Import({CategoryService.class, QueryDslConfiguration.class})
	class DeleteCategoryTest {

		@Autowired
		private CategoryRepository categoryRepository;

		@Autowired
		private CategoryService categoryService;

		@MockBean
		private CategoryChecker categoryChecker;

		@MockBean
		private CardHistoryRepository cardHistoryRepository;

		@Test
		@DisplayName("카테고리 삭제 동작 테스트")
		void deleteCategoryTest() {
			// given
			Category category = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				Id.generateNextId());
			Category savedCategory = categoryRepository.save(category);
			Mockito.doReturn(savedCategory).when(categoryChecker).checkAuthority(any(), any());

			// when
			categoryService.deleteCategory(category.getCategoryId(), category.getMemberId());

			// then
			assertThat(categoryRepository.findAll().stream().filter(cate -> !cate.isDeleted())).isEmpty();
		}

		@Test
		@DisplayName("삭제 요청한 카테고리가 존재하지 않는 경우 예외가 발생하는지 테스트")
		void deleteCategoryWithNotExistedCategoryTest() {
			// given
			Mockito.doThrow(IllegalArgumentException.class)
				.when(categoryChecker)
				.checkAuthority(any(), any());

			// when
			assertThatThrownBy(() -> categoryService.deleteCategory(Id.generateNextId(), Id.generateNextId()))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("삭제 요청한 카테고리의 소유자가 아닌 경우 예외가 발생하는지 테스트")
		void deleteCategoryWithNotOwnerTest() {
			// given
			Mockito.doThrow(AccessDeniedException.class)
				.when(categoryChecker)
				.checkAuthority(any(), any());

			// when
			assertThatThrownBy(() -> categoryService.deleteCategory(Id.generateNextId(), Id.generateNextId()))
				.isInstanceOf(AccessDeniedException.class);
		}
	}

	/**
	 * 카테고리가 없는 경우 contents가 비어있어야 함
	 * 카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함
	 * share와 상관 없이 조회할 수 있어야 한다
	 * 다른 멤버의 카테고리를 조회할 수 없다
	 * 풀이한 카드의 경우 모든 카드의 푼 횟수를 조회한다
	 * 전체 카드는 풀이한 카드 또는 풀이한 카드와 상관 없이 고유한 카드의 갯수를 조회할 수 있어야 한다
	 * searchOption의 containTitle을 입력받은 경우 해당 문자열을 포함하는 카테고리만 조회할 수 있어야 한다
	 */
	@Nested
	@DisplayName("개인 카테고리 페이징 조회")
	@DataJpaTest
	@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	@Import({CategoryService.class, QueryDslConfiguration.class})
	class FindCursorPagingCategoryWithHistoryResponseTest {

		@Autowired
		EntityManager em;

		@Autowired
		private CategoryService categoryService;

		@MockBean
		private CategoryChecker categoryChecker;

		@Test
		@DisplayName("카테고리가 없는 경우 contents가 비어있어야 함")
		void findCursorPagingCategoryWithHistoryResponseWithEmptyCategoryTest() {
			// given
			Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryService.findCursorPagingCategoryWithHistoryResponse(
				10, memberId, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함")
		void findCursorPagingCategoryWithHistoryResponseWithEmptyPageSizeTest() {
			// given
			Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, memberId);
			em.persist(member);
			em.persist(category);

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryService.findCursorPagingCategoryWithHistoryResponse(
				0, memberId, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("share와 상관 없이 조회할 수 있어야 한다")
		void findCursorPagingCategoryWithHistoryResponseWithShareTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id categoryId1 = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Category category1 = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId1);
			em.persist(member);
			em.persist(category);
			em.persist(category1);

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryService.findCursorPagingCategoryWithHistoryResponse(
				10, memberId, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).hasSize(2);
		}

		@Test
		@DisplayName("다른 멤버의 카테고리를 조회할 수 없다")
		void findCursorPagingCategoryWithHistoryResponseWithOtherMemberTest() {
			// given
			Id memberId = Id.generateNextId();
			Id otherMemberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id categoryId1 = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Member otherMember = MemberTestHelper.generateMember(otherMemberId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", otherMemberId, categoryId);
			Category otherCategory = CategoryTestHelper.generateUnSharedCategory("title", otherMemberId, categoryId1);
			em.persist(member);
			em.persist(otherMember);
			em.persist(category);
			em.persist(otherCategory);

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryService.findCursorPagingCategoryWithHistoryResponse(
				10, memberId, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("풀이한 카드의 경우 모든 카드의 푼 횟수를 조회한다")
		void findCursorPagingCategoryWithHistoryResponseWithSolvedCardTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId1));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId1, memberId));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId1, memberId));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId1, memberId));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId, memberId));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryService.findCursorPagingCategoryWithHistoryResponse(
				10, memberId, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents().get(0).getSolveCount()).isEqualTo(4L);
		}

		@Test
		@DisplayName("전체 카드는 풀이한 카드 또는 풀이한 카드와 상관 없이 고유한 카드의 갯수를 조회할 수 있어야 한다")
		void findCursorPagingCategoryWithHistoryResponseWithAllCardTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Id cardId1 = Id.generateNextId();
			Id cardId2 = Id.generateNextId();
			Id cardId3 = Id.generateNextId();
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId1));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId2));
			em.persist(CardTestHelper.genOxCard(memberId, categoryId, cardId3));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId1, memberId));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId1, memberId));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId1, memberId));
			em.persist(CardHistoryTestHelper.generateCardHistory(cardId, memberId));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryService.findCursorPagingCategoryWithHistoryResponse(
				10, memberId, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents().get(0).getTotalCount()).isEqualTo(4L);
		}

		@Test
		@DisplayName("searchOption의 containTitle을 입력받은 경우 해당 문자열을 포함하는 카테고리만 조회할 수 있어야 한다")
		void findCursorPagingCategoryWithHistoryResponseWithContainTitleTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id categoryId1 = Id.generateNextId();
			Id categoryId2 = Id.generateNextId();
			Id categoryId3 = Id.generateNextId();
			em.persist(MemberTestHelper.generateMember(memberId));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title1", memberId, categoryId1));
			em.persist(CategoryTestHelper.generateUnSharedCategory("title2", memberId, categoryId2));
			em.persist(CategoryTestHelper.generateUnSharedCategory("titlz3", memberId, categoryId3));

			// when
			CursorPage<CategoryWithHistoryResponseDto> result = categoryService.findCursorPagingCategoryWithHistoryResponse(
				10, memberId, null, CategorySearchOption.builder().containTitle("title").build());

			// then
			assertThat(result.getContents()).hasSize(3);
		}

	}

	/**
	 * 카테고리가 없는 경우 contents가 비어있어야 함
	 * 카테고리가 있고 내부에 삭제되지 않은 카드가 없다면 카테고리를 조회하면 안됨
	 * 카테고리가 있고 내부에 삭제된 카드가 1개 이상 있어도 contents는 비어 있어야 함
	 * 카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함
	 */
	@Nested
	@DisplayName("공유 카테고리 페이징 조회 테스트")
	@DataJpaTest
	@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
	@Import({CategoryService.class, QueryDslConfiguration.class})
	class FindCursorPagingSharedCategoryResponseDtoTest {

		@Autowired
		EntityManager em;

		@Autowired
		private CategoryRepository categoryRepository;

		@Autowired
		private CategoryService categoryService;

		@Autowired
		private MemberRepository memberRepository;

		@MockBean
		private CategoryChecker categoryChecker;

		@MockBean
		private CardHistoryRepository cardHistoryRepository;

		@Test
		@DisplayName("카테고리가 없는 경우 contents가 비어있어야 함")
		void ShouldReturnZeroWhenNotExistContentsTest() {
			// given
			final int pageSize = 1;

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryService.findCursorPagingSharedCategoryResponseDto(
				pageSize, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 있고 내부에 삭제되지 않은 카드가 없다면 카테고리를 조회하면 안됨")
		void shouldHaveContentsWhenExistCategoryAndExistCardTest() {
			// given
			final int pageSize = 1;
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			memberRepository.save(member);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId,
				Id.generateNextId());
			categoryRepository.save(category);

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryService.findCursorPagingSharedCategoryResponseDto(
				pageSize, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 있고 내부에 삭제된 카드가 1개 이상 있어도 contents는 비어 있어야 함")
		void shouldHaveEmptyContentsWhenExistCategoryAndExistDeletedCardTest() {
			// given
			final int pageSize = 1;
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			memberRepository.save(member);
			Category category = CategoryTestHelper.generateSharedCategory("title", member.getMemberId(),
				Id.generateNextId());
			categoryRepository.save(category);
			Card card = CardTestHelper.genOxCard(memberId, category.getCategoryId(), member.getMemberId());
			card.delete();
			em.persist(card);

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryService.findCursorPagingSharedCategoryResponseDto(
				pageSize, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).isEmpty();
		}

		@Test
		@DisplayName("카테고리가 있는 경우, pageSize가 0인 경우 contents가 비어 있어야 함")
		void shouldContentsIsEmptyWhenPageSizeEqZeroTest() {
			// given
			final int pageSize = 0;
			final Id memberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			memberRepository.save(member);
			Category category = CategoryTestHelper.generateSharedCategory("title", member.getMemberId(),
				Id.generateNextId());
			categoryRepository.save(category);

			// when
			CursorPage<SharedCategoryResponseDto> result = categoryService.findCursorPagingSharedCategoryResponseDto(
				pageSize, null, CategorySearchOption.builder().build());

			// then
			assertThat(result.getContents()).isEmpty();
		}
	}

}