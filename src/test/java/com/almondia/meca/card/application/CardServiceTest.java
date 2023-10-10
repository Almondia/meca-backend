package com.almondia.meca.card.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.controller.dto.CardCountAndShareResponseDto;
import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.CardWithStatisticsDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.repository.KeywordCardRepository;
import com.almondia.meca.card.domain.repository.MultiChoiceCardRepository;
import com.almondia.meca.card.domain.repository.OxCardRepository;
import com.almondia.meca.card.domain.service.CardChecker;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.card.infra.querydsl.CardSearchOption;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.helper.recommend.CategoryRecommendTestHelper;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.recommand.domain.entity.CategoryRecommend;

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

	@Autowired
	CardHistoryRepository cardHistoryRepository;

	@PersistenceContext
	EntityManager em;

	private void persistAll(Object... objects) {
		for (Object object : objects) {
			em.persist(object);
		}
	}

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
			assertThat(all.get(0))
				.hasFieldOrPropertyWithValue("oxAnswer", OxAnswer.O)
				.hasFieldOrPropertyWithValue("question", "[\"<p>asdsa</p>\",\"1\",\"2\",\"3\",\"4\",\"5\"]")
				.hasFieldOrPropertyWithValue("title", new Title("title"));
		}

		@Test
		@DisplayName("keywordCard type 정보가 들어가면 keywordCard 정보가 저장되는지 검증")
		void shouldSaveKeywordCardTest() {
			cardService.saveCard(makeSaveCardRequest()
				.answer("asdf")
				.cardType(CardType.KEYWORD).build(), Id.generateNextId());
			List<KeywordCard> all = keywordCardRepository.findAll();
			assertThat(all).isNotEmpty();
			assertThat(all.get(0))
				.hasFieldOrPropertyWithValue("keywordAnswer", new KeywordAnswer("asdf"))
				.hasFieldOrPropertyWithValue("question", "[\"<p>asdsa</p>\",\"1\",\"2\",\"3\",\"4\",\"5\"]")
				.hasFieldOrPropertyWithValue("title", new Title("title"));
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
			assertThat(all.get(0))
				.hasFieldOrPropertyWithValue("multiChoiceAnswer", new MultiChoiceAnswer(1))
				.hasFieldOrPropertyWithValue("question", "[\"<p>asdsa</p>\",\"1\",\"2\",\"3\",\"4\",\"5\"]")
				.hasFieldOrPropertyWithValue("title", new Title("title"));
		}

		@Test
		@DisplayName("요청시 editText 속성이 null이더라도 정삭 동작해야 함")
		void shouldSaveCardWithNullEditTextTest() {
			cardService.saveCard(
				makeSaveCardRequestWithoutEditText()
					.description(null)
					.answer(OxAnswer.O.toString())
					.cardType(CardType.OX_QUIZ).build(),
				Id.generateNextId());
			List<Card> all = cardRepository.findAll();
			assertThat(all).isNotEmpty();
		}

		private SaveCardRequestDto.SaveCardRequestDtoBuilder makeSaveCardRequest() {
			return SaveCardRequestDto.builder()
				.title(new Title("title"))
				.question("[\"<p>asdsa</p>\",\"1\",\"2\",\"3\",\"4\",\"5\"]")
				.categoryId(Id.generateNextId())
				.description(new Description("hello"));
		}

		private SaveCardRequestDto.SaveCardRequestDtoBuilder makeSaveCardRequestWithoutEditText() {
			return SaveCardRequestDto.builder()
				.title(new Title("title"))
				.question("question")
				.categoryId(Id.generateNextId());
		}
	}

	@Nested
	@DisplayName("카드 업데이트 테스트")
	class UpdateCardTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		Id cardId = Id.generateNextId();

		@Test
		@DisplayName("카드 업데이트시 업데이트가 성공적으로 반영되었는지 테스트")
		void shouldReturnCardResponseDtoWhenCallUpdateCardRequestTest() {
			// given
			KeywordCard card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			UpdateCardRequestDto updateCardRequestDto = makeUpdateCardRequest(categoryId);
			em.persist(card);
			em.persist(category);
			em.persist(member);

			// when
			CardDto responseDto = cardService.updateCard(updateCardRequestDto, cardId, memberId);

			// then
			assertThat(responseDto).isInstanceOf(CardDto.class);
		}

		@Test
		@DisplayName("본인의 카테고리가 아닌 남의 카테고리로 카드 카테고리 업데이트시 권한 에러")
		void shouldThrowAccessDeniedExceptionWhenCallNotMyCategoryTest() {
			// given
			KeywordCard card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			UpdateCardRequestDto updateCardRequestDto = makeUpdateCardRequest(categoryId);
			Id randomCardId = Id.generateNextId();
			em.persist(card);
			em.persist(category);
			em.persist(member);

			assertThatThrownBy(() -> cardService.updateCard(updateCardRequestDto, randomCardId, memberId))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("본인의 카드가 아닌 다른 카드 ID를 가지고 요청한 경우 권한 에러")
		void shouldThrowAccessDeniedExceptionWhenCallNotMyCardTest() {
			// given
			KeywordCard card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			em.persist(card);
			em.persist(category);
			em.persist(member);
			UpdateCardRequestDto updateCardRequestDto = makeUpdateCardRequest(Id.generateNextId());

			assertThatThrownBy(() -> cardService.updateCard(updateCardRequestDto, cardId, memberId))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("title만 요청한 경우 title만 수정해야됨")
		void shouldUpdateTitleOnlyTest() {
			// given
			KeywordCard card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			em.persist(card);
			em.persist(category);
			em.persist(member);

			UpdateCardRequestDto updateCardRequestDto = UpdateCardRequestDto.builder()
				.title(new Title("title2"))
				.build();

			// when
			cardService.updateCard(updateCardRequestDto, cardId, memberId);

			// then
			List<Card> all = cardRepository.findAll();
			assertThat(all).isNotEmpty();
			assertThat(all.get(0))
				.hasFieldOrPropertyWithValue("title", new Title("title2"));
		}

		@Test
		@DisplayName("question만 요청한 경우 question만 수정해야됨")
		void shouldUpdateQuestionOnlyTest() {
			// given
			KeywordCard card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			em.persist(card);
			em.persist(category);
			em.persist(member);

			UpdateCardRequestDto updateCardRequestDto = UpdateCardRequestDto.builder()
				.question("question2")
				.build();

			// when
			cardService.updateCard(updateCardRequestDto, cardId, memberId);

			// then
			List<Card> all = cardRepository.findAll();
			assertThat(all).isNotEmpty();
			assertThat(all.get(0))
				.hasFieldOrPropertyWithValue("question", "question2");
		}

		@Test
		@DisplayName("editText만 요청한 경우 editText만 수정해야됨")
		void shouldUpdateEditTextOnlyTest() {
			// given
			KeywordCard card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			em.persist(card);
			em.persist(category);
			em.persist(member);

			UpdateCardRequestDto updateCardRequestDto = UpdateCardRequestDto.builder()
				.description(new Description("edit text2"))
				.build();

			// when
			cardService.updateCard(updateCardRequestDto, cardId, memberId);

			// then
			List<Card> all = cardRepository.findAll();
			assertThat(all).isNotEmpty();
			assertThat(all.get(0))
				.hasFieldOrPropertyWithValue("description", new Description("edit text2"));
		}

		private UpdateCardRequestDto makeUpdateCardRequest(Id categoryId) {
			return UpdateCardRequestDto.builder()
				.title(new Title("title"))
				.description(new Description("edit text"))
				.question("question")
				.categoryId(categoryId)
				.build();
		}

		@Test
		@DisplayName("정답 업데이트시 oxCard 타입에 맞게 validation되며 업데이트 되야함")
		void shouldThrowIllegalArgumentExceptionWhenCallUpdateOxAnswerTest() {
			// given
			OxCard card = CardTestHelper.genOxCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			UpdateCardRequestDto updateOxAnswerRequestDto = UpdateCardRequestDto.builder()
				.answer("A")
				.build();
			em.persist(member);
			em.persist(card);
			em.persist(category);

			// expect
			assertThatThrownBy(() -> cardService.updateCard(updateOxAnswerRequestDto, cardId, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("정답 업데이트시 keywordCard 타입에 맞게 validation되며 업데이트 되야함")
		void shouldThrowIllegalArgumentExceptionWhenCallUpdateKeywordAnswerTest() {
			// given
			KeywordCard card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			UpdateCardRequestDto updateKeywordAnswerRequestDto = UpdateCardRequestDto.builder()
				.answer("A")
				.build();
			em.persist(member);
			em.persist(card);
			em.persist(category);

			// when
			CardDto updateCard = cardService.updateCard(updateKeywordAnswerRequestDto, cardId, memberId);

			// then
			assertThat(updateCard.getAnswer()).isEqualTo("A");
		}

		@Test
		@DisplayName("정답 업데이트시 multiChoiceCard 타입에 맞게 validation되며 업데이트 되야함")
		void shouldThrowIllegalArgumentExceptionWhenCallUpdateMultiChoiceAnswerTest() {
			// given
			MultiChoiceCard card = CardTestHelper.genMultiChoiceCard(memberId, categoryId, cardId);
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			UpdateCardRequestDto updateMultiChoiceAnswerRequestDto = UpdateCardRequestDto.builder()
				.answer("A")
				.build();
			em.persist(member);
			em.persist(card);
			em.persist(category);

			// expect
			assertThatThrownBy(() -> cardService.updateCard(updateMultiChoiceAnswerRequestDto, cardId, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("카드 커서 페이징 조회")
	class SearchCardCursorPagingTest {

		@Test
		@DisplayName("본인이 가진 카테고리가 아닐 시 권한 에러 출력")
		void shouldThrowAccessDeniedExceptionWhenNotMyCategoryIdHandleTest() {
			Id randomCategoryId = Id.generateNextId();
			Id randomMemberId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(randomMemberId);
			CardSearchOption cardSearchOption = CardSearchOption.builder().build();
			assertThatThrownBy(() -> cardService.searchCursorPagingCard(
				10,
				null,
				randomCategoryId,
				member,
				cardSearchOption)).isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("카드 커서 페이징 출력 형태 및 결과 테스트")
		void shouldSuccessWorkTest() {
			//given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			Card card = CardTestHelper.genKeywordCard(memberId, categoryId, Id.generateNextId());
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), card.getCardId(),
				10);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), card.getCardId(),
				20);
			CategoryRecommend categoryRecommend = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				Id.generateNextId());
			persistAll(category, card, cardHistory1, cardHistory2, categoryRecommend);

			// when
			CursorPage<CardWithStatisticsDto> cardCursorPageWithCategory = cardService.searchCursorPagingCard(
				10,
				null,
				categoryId,
				MemberTestHelper.generateMember(memberId),
				CardSearchOption.builder().build());

			// then
			assertThat(cardCursorPageWithCategory.getContents().get(0).getCard().getCardId()).isEqualTo(
				card.getCardId());
			assertThat(cardCursorPageWithCategory.getContents().get(0).getStatistics().getScoreAvg()).isEqualTo(15);

		}
	}

	@Nested
	@DisplayName("카드 삭제")
	class CardDeleteTest {

		@Test
		@DisplayName("권한 체크를 수행하는지 테스트")
		void checkAuthorityTest() {
			Id randomCardId = Id.generateNextId();
			Id randomMemberId = Id.generateNextId();

			assertThatThrownBy(() -> cardService.deleteCard(randomCardId, randomMemberId))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("카드 삭제시 카드 히스토리도 모두 삭제된다")
		void shouldDeleteCardHistoriesAllRelatedCardTest() {
			// given
			Id memberId = Id.generateNextId();
			Id memberId2 = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Card card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			CardHistory cardHistory1 = CardHistoryTestHelper.generateCardHistory(cardId, memberId);
			CardHistory cardHistory2 = CardHistoryTestHelper.generateCardHistory(cardId, memberId2);
			persistAll(card, cardHistory1, cardHistory2);

			// when
			cardService.deleteCard(cardId, memberId);

			// then
			List<CardHistory> histories = cardHistoryRepository.findByCardId(cardId);
			Card resultCard = cardRepository.findById(cardId).orElseThrow();
			assertThat(resultCard).extracting("isDeleted").isEqualTo(true);
			assertThat(histories.get(0)).extracting("isDeleted").isEqualTo(true);
			assertThat(histories.get(1)).extracting("isDeleted").isEqualTo(true);

		}
	}

	@Nested
	@DisplayName("회원 카드 단일 조회")
	class SearchCardOneTest {

		Id memberId = Id.generateNextId();
		Id categoryId = Id.generateNextId();
		Id cardId1 = Id.generateNextId();

		@Test
		@DisplayName("권한 체크 수행 여부 테스트")
		void checkAuthorityTest() {
			Id randomCardId = Id.generateNextId();
			Id randomMemberId = Id.generateNextId();

			assertThatThrownBy(() -> cardService.findCardById(randomCardId, randomMemberId))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("종류별 카드 타입 변환이 결과에 잘 반영 되는지 확인")
		void returnCardResponseType() {
			initDataSetting();
			CardDto card = cardService.findCardById(cardId1, memberId);
			assertThat(card.getCardType()).isEqualTo(CardType.MULTI_CHOICE);
		}

		@Test
		@DisplayName("카드가 존재하지 않을 경우 예외 발생")
		void shouldThrowExceptionWhenCardIsDeleted() {
			initDataSetting();
			Card card = cardRepository.findById(cardId1).orElseThrow();
			card.delete();
			assertThatThrownBy(() -> cardService.findCardById(cardId1, memberId))
				.isInstanceOf(IllegalArgumentException.class);
		}

		private void initDataSetting() {
			cardRepository.saveAll(List.of(
				CardTestHelper.genMultiChoiceCard(memberId, categoryId, cardId1)
			));
		}
	}

	@Nested
	@DisplayName("공유 카드 조회")
	class FindSharedCardTest {

		@Test
		@DisplayName("공유 카드 조회시 카테고리가 공유되지 않았을 경우 예외 발생")
		void shouldThrowExceptionWhenCategoryIsNotSharedTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			persistAll(category);

			// when
			assertThatThrownBy(() -> cardService.findSharedCard(cardId))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("공유된 카테고리의 카드가 존재하지 않습니다");
		}

		@Test
		@DisplayName("공유 카드 조회시 카테고리가 삭제되었을 경우 예외 발생")
		void shouldThrowExceptionWhenCategoryIsDeletedTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			category.delete();
			persistAll(category);

			// when
			assertThatThrownBy(() -> cardService.findSharedCard(cardId))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("공유된 카테고리의 카드가 존재하지 않습니다");
		}

		@Test
		@DisplayName("공유 카드 조회시 정상적인 dto 리턴해야함")
		void shouldReturnCardDtoWhenCallFindSharedCardTest() {
			// given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Id cardId = Id.generateNextId();
			Member member = MemberTestHelper.generateMember(memberId);
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			Card card = CardTestHelper.genKeywordCard(memberId, categoryId, cardId);
			persistAll(member, category, card);

			// when
			CardResponseDto result = cardService.findSharedCard(cardId);

			// then
			assertThat(result).isInstanceOf(CardResponseDto.class);
		}
	}

	@Nested
	@DisplayName("카테고리별 카드 개수 조회 API")
	class SearchCardsCountByCategoryIdTest {

		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();

		@Test
		@DisplayName("내 카테고리에 카드 갯수를 요청한 경우 정상적으로 카드 개수를 출력")
		void shouldReturnCountWhenMyCategoryTest() {
			// given
			Category myCategory = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			Card card1 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, Id.generateNextId());
			Card card2 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, Id.generateNextId());
			persistAll(myCategory, card1, card2);

			// when
			CardCountAndShareResponseDto result = cardService.findCardsCountAndSharedByCategoryId(categoryId, memberId);

			// then
			assertThat(result).extracting("count").isEqualTo(2L);
		}

		@Test
		@DisplayName("내 카테고리가 아닌 카테고리에 카드 갯수를 요청한 경우 공유 상태의 카테고리가 아닌경우 권한 에러 발생")
		void shouldThrowExceptionWhenNotMyCategoryAndNotSharedTest() {
			// given
			Category notMyCategory = CategoryTestHelper.generateUnSharedCategory("title", Id.generateNextId(),
				categoryId);
			Card card1 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, Id.generateNextId());
			Card card2 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, Id.generateNextId());
			persistAll(notMyCategory, card1, card2);

			// when
			assertThatThrownBy(() -> cardService.findCardsCountAndSharedByCategoryId(categoryId, memberId))
				.isInstanceOf(AccessDeniedException.class);
		}

		@Test
		@DisplayName("내 카테고리가 아닌 카테고리에 카드 갯수를 요청한 경우 공유 상태라면 정상적인 카드 갯수 출력")
		void shouldReturnCountWhenNotMyCategoryAndSharedTest() {
			// given
			Category notMyCategory = CategoryTestHelper.generateSharedCategory("title", Id.generateNextId(),
				categoryId);
			Card card1 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, Id.generateNextId());
			Card card2 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, Id.generateNextId());
			persistAll(notMyCategory, card1, card2);

			// when
			CardCountAndShareResponseDto result = cardService.findCardsCountAndSharedByCategoryId(categoryId, memberId);

			// then
			assertThat(result).extracting("count").isEqualTo(2L);
		}
	}

	@Nested
	@DisplayName("searchCursorPagingSharedCard 테스트")
	class SearchCursorPagingSharedCardTest {

		@Test
		@DisplayName("공유 카테고리가 존재하지 않으면 예외를 발생한다")
		void shouldThrowExceptionWhenSharedCategoryNotExistTest() {
			// given
			CardSearchOption cardSearchOption = CardSearchOption.builder().build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId);
			persistAll(category);

			// when
			assertThatThrownBy(
				() -> cardService.searchCursorPagingSharedCard(10, null, categoryId, cardSearchOption))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("공유되지 않은 카테고리에 접근할 수 없습니다");
		}

		@Test
		@DisplayName("카테고리가 삭제되어 있다면 예외를 발생한다")
		void shouldThrowExceptionWhenSharedCategoryIsDeletedTest() {
			// given
			CardSearchOption cardSearchOption = CardSearchOption.builder().build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			category.delete();
			persistAll(category);

			// when
			assertThatThrownBy(
				() -> cardService.searchCursorPagingSharedCard(10, null, categoryId, cardSearchOption))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("공유되지 않은 카테고리에 접근할 수 없습니다");
		}

		@Test
		@DisplayName("삭제된 회원인 경우 예외를 발생한다")
		void shouldThrowExceptionWhenMemberIsDeletedTest() {
			// given
			CardSearchOption cardSearchOption = CardSearchOption.builder().build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			member.delete();
			persistAll(category, member);

			// when
			assertThatThrownBy(
				() -> cardService.searchCursorPagingSharedCard(10, null, categoryId, cardSearchOption))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("삭제된 멤버에 접근할 수 없습니다");
		}

		@Test
		@DisplayName("통계 기록은 있으면 안되고 모두 0 이여야 함")
		void shouldReturnZeroWhenStatisticsExistTest() {
			// given
			CardSearchOption cardSearchOption = CardSearchOption.builder().build();
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			Card card1 = CardTestHelper.genOxCard(Id.generateNextId(), categoryId, memberId);
			CategoryRecommend categoryRecommend = CategoryRecommendTestHelper.generateCategoryRecommend(categoryId,
				Id.generateNextId());
			persistAll(member, category, card1, categoryRecommend);

			// when
			CardCursorPageWithCategory cardCursorPageWithCategory = cardService.searchCursorPagingSharedCard(10, null,
				categoryId, cardSearchOption);

			// then
			assertThat(cardCursorPageWithCategory.getContents().get(0).getStatistics().getScoreAvg()).isEqualTo(0.0);
			assertThat(cardCursorPageWithCategory.getContents().get(0).getStatistics().getTryCount()).isZero();
		}
	}
}