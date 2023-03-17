package com.almondia.meca.card.sevice;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Image;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.card.infra.querydsl.CardSearchCriteria;
import com.almondia.meca.card.infra.querydsl.CardSortField;
import com.almondia.meca.card.repository.CardRepository;
import com.almondia.meca.card.repository.KeywordCardRepository;
import com.almondia.meca.card.repository.MultiChoiceCardRepository;
import com.almondia.meca.card.repository.OxCardRepository;
import com.almondia.meca.card.sevice.checker.CardChecker;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.service.checker.CategoryChecker;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.data.CardDataFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CardService.class, QueryDslConfiguration.class, CategoryChecker.class, CardChecker.class})
class CardServiceTest {

	@Autowired
	CardService cardService;

	@Autowired
	CardRepository cardRepository;

	@Autowired
	OxCardRepository oxCardRepository;

	@Autowired
	KeywordCardRepository keywordCardRepository;

	@Autowired
	MultiChoiceCardRepository multiChoiceCardRepository;

	@PersistenceContext
	EntityManager em;

	/**
	 * 1. oxCard type정보가 들어가면 oxCard 정보가 저장되는지 검증
	 * 2. keywordCard type 정보가 들어가면 keywordCard 정보가 저장되는지 검증
	 * 3. multi choice card type 정보가 들어가면 MultiChoiceCard 정보가 저장되는 지 검증
	 */
	@Nested
	@DisplayName("카드 저장 테스트")
	class SaveCardTest {
		@Test
		@DisplayName("oxCard type정보가 들어가면 oxCard 정보가 저장되는지 검증")
		void shouldSaveOxCardTest() {
			cardService.saveCard(makeSaveCardRequest().answer(OxAnswer.O.toString()).cardType(CardType.OX_QUIZ).build(),
				Id.generateNextId());
			List<OxCard> all = oxCardRepository.findAll();
			assertThat(all).isNotEmpty();
		}

		@Test
		@DisplayName("keywordCard type 정보가 들어가면 keywordCard 정보가 저장되는지 검증")
		void shouldSaveKeywordCardTest() {
			cardService.saveCard(makeSaveCardRequest()
				.answer("asdf")
				.cardType(CardType.KEYWORD).build(), Id.generateNextId());
			List<KeywordCard> all = keywordCardRepository.findAll();
			assertThat(all).isNotEmpty();
		}

		@Test
		@DisplayName("multi choice card type 정보가 들어가면 MultiChoiceCard 정보가 저장되는 지 검증")
		void shouldSaveMultiCardTest() {
			cardService.saveCard(
				makeSaveCardRequest().answer("1")
					.cardType(CardType.MULTI_CHOICE).build(),
				Id.generateNextId());
			List<MultiChoiceCard> all = multiChoiceCardRepository.findAll();
			assertThat(all).isNotEmpty();
		}

		private SaveCardRequestDto.SaveCardRequestDtoBuilder makeSaveCardRequest() {
			return SaveCardRequestDto.builder()
				.title(new Title("title"))
				.question(new Question("question"))
				.categoryId(Id.generateNextId())
				.images("A,B,C");
		}
	}

	/**
	 * 1. 카드 업데이트시 업데이트가 성공적으로 반영되었는지 테스트
	 * 2. 본인의 카테고리가 아닌 남의 카테고리로 카드 카테고리 업데이트시 권한 에러
	 * 3. 본인의 카드가 아닌 다른 카드 ID를 가지고 요청한 경우 권한 에러
	 */
	@Nested
	@DisplayName("카드 업데이트 테스트")
	class UpdateCardTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		Id cardId = Id.generateNextId();

		@BeforeEach
		void before() {
			persistCardEntity();
		}

		@AfterEach
		void after() {
			em.clear();
		}

		@Test
		@DisplayName("카드 업데이트시 업데이트가 성공적으로 반영되었는지 테스트")
		void shouldReturnCardResponseDtoWhenCallUpdateCardRequestTest() {
			UpdateCardRequestDto updateCardRequestDto = makeUpdateCardRequest(categoryId);
			CardResponseDto responseDto = cardService.updateCard(updateCardRequestDto, cardId, memberId);
			assertThat(responseDto).isInstanceOf(CardResponseDto.class);
		}

		@Test
		@DisplayName("본인의 카테고리가 아닌 남의 카테고리로 카드 카테고리 업데이트시 권한 에러")
		void shouldThrowAccessDeniedExceptionWhenCallNotMyCategoryTest() {
			assertThatThrownBy(() -> {
				UpdateCardRequestDto updateCardRequestDto = makeUpdateCardRequest(categoryId);
				cardService.updateCard(updateCardRequestDto, Id.generateNextId(), memberId);
			}).isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("본인의 카드가 아닌 다른 카드 ID를 가지고 요청한 경우 권한 에러")
		void shouldThrowAccessDeniedExceptionWhenCallNotMyCardTest() {
			assertThatThrownBy(() -> {
				UpdateCardRequestDto updateCardRequestDto = makeUpdateCardRequest(Id.generateNextId());
				cardService.updateCard(updateCardRequestDto, cardId, memberId);
			}).isInstanceOf(AccessDeniedException.class);
		}

		private UpdateCardRequestDto makeUpdateCardRequest(Id categoryId) {
			return UpdateCardRequestDto.builder()
				.title(new Title("title"))
				.images("A,B,C")
				.question(new Question("question"))
				.categoryId(categoryId)
				.cardType(CardType.OX_QUIZ)
				.build();
		}

		private void persistCardEntity() {
			oxCardRepository.save(OxCard.builder()
				.title(new Title("title"))
				.images(List.of(new Image("A"), new Image("B")))
				.cardId(cardId)
				.memberId(memberId)
				.categoryId(categoryId)
				.cardType(CardType.OX_QUIZ)
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.build());
			em.persist(Category.builder()
				.categoryId(categoryId)
				.title(new com.almondia.meca.category.domain.vo.Title("category title"))
				.memberId(memberId)
				.build());
		}
	}

	/**
	 * 1. 본인이 가진 카테고리가 아닐 시 권한 에러 출력
	 * 2. 카드 커서 페이징 출력 형태 및 결과 테스트
	 */
	@Nested
	@DisplayName("카드 커서 페이징 조회")
	class SearchCardCursorPagingTest {
		CardDataFactory cardDataFactory = new CardDataFactory();
		Id memberId = cardDataFactory.getMemberId();
		Id categoryId = cardDataFactory.getCategoryId();

		@BeforeEach
		void before() {
			List<Card> testData = cardDataFactory.createTestData();
			cardRepository.saveAll(testData);
		}

		@Test
		@DisplayName("본인이 가진 카테고리가 아닐 시 권한 에러 출력")
		void shouldThrowAccessDeniedExceptionWhenNotMyCategoryIdHandleTest() {

			assertThatThrownBy(() -> cardService.searchCursorPagingCard(
				10,
				categoryId,
				CardSearchCriteria.builder().build(),
				SortOption.of(CardSortField.CARD_ID, SortOrder.DESC),
				Id.generateNextId())).isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("카드 커서 페이징 출력 형태 및 결과 테스트")
		void shouldSuccessWorkTest() {
			em.persist(Category.builder()
				.title(new com.almondia.meca.category.domain.vo.Title("title"))
				.memberId(memberId)
				.categoryId(categoryId)
				.build());
			CursorPage<CardResponseDto> cursorPage = cardService.searchCursorPagingCard(
				5,
				categoryId,
				CardSearchCriteria.builder().build(),
				SortOption.of(CardSortField.CARD_ID, SortOrder.DESC),
				memberId);
			assertThat(cursorPage)
				.hasFieldOrProperty("contents")
				.hasFieldOrProperty("pageSize")
				.hasFieldOrProperty("hasNext")
				.hasFieldOrProperty("sortOrder");
		}
	}
}