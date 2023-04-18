package com.almondia.meca.card.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.almondia.meca.card.application.CardService;
import com.almondia.meca.card.application.CardSimulationService;
import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardCursorPageWithSharedCategoryDto;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
@Import({JacksonConfiguration.class})
class CardControllerTest {

	@MockBean
	CardService cardService;

	@MockBean
	CardSimulationService cardSimulationService;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	WebApplicationContext context;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).build();
	}

	/**
	 * 1. 요청 성공시 201을 리턴하고 카드 정보를 반환한다.
	 */
	@Nested
	@DisplayName("카드 저장 API 테스트")
	class saveCardTest {

		@Test
		@WithMockMember
		@DisplayName("요청 성공시 201을 리턴하고 카드 정보를 반환한다")
		void test() throws Exception {
			SaveCardRequestDto saveCardRequestDto = SaveCardRequestDto.builder()
				.title(new Title("title"))
				.question(new Question("hello"))
				.description(new Description("hello"))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.answer(OxAnswer.O.toString())
				.build();
			Mockito.doReturn(makeResponse()).when(cardService).saveCard(any(), any());

			mockMvc.perform(post("/api/v1/cards").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(saveCardRequestDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("cardId").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("question").exists())
				.andExpect(jsonPath("description").exists())
				.andExpect(jsonPath("categoryId").exists())
				.andExpect(jsonPath("cardType").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("answer").exists());
		}

		private CardResponseDto makeResponse() {
			return CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.memberId(Id.generateNextId())
				.question(new Question("hello"))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.answer(OxAnswer.O.name())
				.description(new Description("hello"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
		}
	}

	/**
	 * 1. 성공시 200 응답코드와 리턴해야할 값 검증
	 */
	@Nested
	@DisplayName("카드 업데이트 API 테스트")
	class UpdateCardTest {

		@Test
		@WithMockMember
		@DisplayName("성공시 200 응답코드와 리턴해야할 값 검증")
		void shouldReturn200WhenSuccessTest() throws Exception {
			UpdateCardRequestDto requestDto = UpdateCardRequestDto.builder()
				.title(new Title("title"))
				.question(new Question("question"))
				.description(new Description("editText"))
				.categoryId(Id.generateNextId())
				.build();
			Mockito.doReturn(makeResponse()).when(cardService).updateCard(any(), any(), any());

			mockMvc.perform(
					put("/api/v1/cards/{cardId}", Id.generateNextId().toString()).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("cardId").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("question").exists())
				.andExpect(jsonPath("description").exists())
				.andExpect(jsonPath("categoryId").exists())
				.andExpect(jsonPath("cardType").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("answer").exists());
		}

		private CardResponseDto makeResponse() {
			return CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.memberId(Id.generateNextId())
				.question(new Question("hello"))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.answer(OxAnswer.O.name())
				.description(new Description("hello"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
		}
	}

	/**
	 * 1. 성공시 200 응답코드와 리턴해야할 값 검증
	 */
	@Nested
	@DisplayName("카드 커서 페이징 조회 API")
	class CardCursorPagingSearchTest {

		@Test
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			List<CardResponseDto> contents = List.of(makeResponse());
			CardCursorPageWithCategory cardCursorPageWithCategory = new CardCursorPageWithCategory(contents,
				Id.generateNextId(), 5, SortOrder.DESC);
			cardCursorPageWithCategory.setCategory(Category.builder()
				.categoryId(Id.generateNextId())
				.title(new com.almondia.meca.category.domain.vo.Title("title"))
				.build());
			Mockito.doReturn(cardCursorPageWithCategory)
				.when(cardService)
				.searchCursorPagingCard(anyInt(), any(), any(), any(), any());
			mockMvc.perform(
					get("/api/v1/cards/categories/{categoryId}/me?pageSize=100&sortOrder=desc", Id.generateNextId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("contents").exists())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andExpect(jsonPath("category").exists());
		}

		private CardResponseDto makeResponse() {
			return CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.memberId(Id.generateNextId())
				.question(new Question("hello"))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.answer(OxAnswer.O.name())
				.description(new Description("hello"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
		}
	}

	/**
	 * 1. 정상 동작시 200 응답 테스트
	 */
	@Nested
	@DisplayName("카드 삭제 API")
	class DeleteCardTest {

		@Test
		@DisplayName("정상 동작시 200 응답 테스트")
		@WithMockMember
		void shouldReturnOkWhenCallSuccessTest() throws Exception {
			mockMvc.perform(delete("/api/v1/cards/{cardId}", Id.generateNextId()))
				.andExpect(status().isOk());
		}
	}

	/**
	 * 정상 동작시 200 응답 및 응답 포맷 테스트
	 */
	@Nested
	@DisplayName("회원 카드 단일 조회 API")
	class SearchCardOneTest {

		@Test
		@WithMockMember
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OKAndResponseFormatTest() throws Exception {
			CardResponseDto responseDto = CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.question(new Question("question"))
				.cardType(CardType.OX_QUIZ)
				.answer("O")
				.description(new Description("hello"))
				.title(new Title("title1"))
				.build();
			Mockito.doReturn(responseDto).when(cardService).findCardById(any(), any());
			mockMvc.perform(get("/api/v1/cards/{cardId}/me", Id.generateNextId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("cardId").exists())
				.andExpect(jsonPath("question").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("cardType").exists())
				.andExpect(jsonPath("answer").exists())
				.andExpect(jsonPath("description").exists())
				.andExpect(jsonPath("title").exists());
		}
	}

	/**
	 *  1. 정상 동작시 200 응답 및 응답 포맷 테스트
	 */
	@Nested
	@DisplayName("카드 시뮬레이션 테스트")
	class CardSimulationTest {

		@Test
		@WithMockMember
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void cardSimulationTest() throws Exception {
			CardResponseDto responseDto = CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.question(new Question("question"))
				.cardType(CardType.OX_QUIZ)
				.answer("O")
				.description(new Description("hello"))
				.title(new Title("title1"))
				.build();
			Mockito.doReturn(List.of(responseDto)).when(cardSimulationService).simulateRandom(any(), any(), anyInt());
			mockMvc.perform(
					get("/api/v1/cards/categories/{categoryId}/simulation?limit=3&algorithm=random", Id.generateNextId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cardId").exists())
				.andExpect(jsonPath("$[0].memberId").exists())
				.andExpect(jsonPath("$[0].question").exists())
				.andExpect(jsonPath("$[0].cardType").exists())
				.andExpect(jsonPath("$[0].answer").exists())
				.andExpect(jsonPath("$[0].description").exists())
				.andExpect(jsonPath("$[0].title").exists());
		}
	}

	@Nested
	@DisplayName("카테고리 별 카드 갯수 조회 API")
	class FindCardCountByCategoryTest {

		@Test
		@WithMockMember
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OKAndResponseFormatTest() throws Exception {
			Mockito.doReturn(1L)
				.when(cardService).findCardsCountByCategoryId(any(), any());
			mockMvc.perform(get("/api/v1/cards//categories/{categoryId}/me/count", Id.generateNextId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("count").exists());
		}
	}

	@Nested
	@DisplayName("공유 카드 단일 조회 API")
	class FindSharedCardTest {

		@Test
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OkAndResponseFormatTest() throws Exception {
			OxCard card = OxCard.builder()
				.cardId(Id.generateNextId())
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.title(new Title("title"))
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.description(new Description("description"))
				.build();
			Member member = Member.builder()
				.memberId(Id.generateNextId())
				.email(new Email("abc@gamil.com"))
				.name(new Name("nickname"))
				.oAuthType(OAuthType.KAKAO)
				.oauthId("id")
				.role(Role.USER)
				.build();
			Mockito.doReturn(new SharedCardResponseDto(card, member))
				.when(cardService).findSharedCard(any());

			mockMvc.perform(get("/api/v1/cards/{cardId}/share", Id.generateNextId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cardInfo.cardId").exists())
				.andExpect(jsonPath("$.cardInfo.categoryId").exists())
				.andExpect(jsonPath("$.cardInfo.title").exists())
				.andExpect(jsonPath("$.cardInfo.question").exists())
				.andExpect(jsonPath("$.cardInfo.cardType").exists())
				.andExpect(jsonPath("$.cardInfo.answer").exists())
				.andExpect(jsonPath("$.cardInfo.description").exists())
				.andExpect(jsonPath("$.cardInfo.title").exists())
				.andExpect(jsonPath("$.memberInfo.memberId").exists())
				.andExpect(jsonPath("$.memberInfo.email").exists())
				.andExpect(jsonPath("$.memberInfo.name").exists())
				.andExpect(jsonPath("$.memberInfo.oauthType").exists())
				.andExpect(jsonPath("$.memberInfo.role").exists());
		}
	}

	@Nested
	@DisplayName("공유 카테고리의 카드 커서 페이징 조회 API")
	class SharedCardCursorPagingTest {

		@Test
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OkAndResponseFormatTest() throws Exception {
			List<SharedCardResponseDto> contents = List.of(makeResponse());
			CardCursorPageWithSharedCategoryDto cardCursorPageWithCategory = new CardCursorPageWithSharedCategoryDto(
				contents,
				Id.generateNextId(), 5, SortOrder.DESC);
			cardCursorPageWithCategory.setCategory(Category.builder()
				.categoryId(Id.generateNextId())
				.title(new com.almondia.meca.category.domain.vo.Title("title"))
				.build());
			Mockito.doReturn(cardCursorPageWithCategory)
				.when(cardService)
				.searchCursorPagingSharedCard(anyInt(), any(), any(), any());
			mockMvc.perform(
					get("/api/v1/cards/categories/{categoryId}/share?pageSize=100&sortOrder=desc", Id.generateNextId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("contents").exists())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andExpect(jsonPath("category").exists());
		}

		private SharedCardResponseDto makeResponse() {
			Card card = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
			Member member = MemberTestHelper.generateMember(Id.generateNextId());
			return new SharedCardResponseDto(card, member);
		}
	}
}