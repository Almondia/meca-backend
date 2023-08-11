package com.almondia.meca.card.controller;

import static com.almondia.meca.asciidocs.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.almondia.meca.asciidocs.fields.DocsFieldGeneratorUtils;
import com.almondia.meca.card.application.CardService;
import com.almondia.meca.card.application.CardSimulationService;
import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardCountGroupByScoreDto;
import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.CardWithStatisticsDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.cardhistory.controller.dto.CardStatisticsDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.configuration.asciidocs.DocsFieldGeneratorUtilsConfiguration;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({JacksonConfiguration.class, DocsFieldGeneratorUtilsConfiguration.class})
class CardControllerTest {

	private static final String jwtToken = "Bearer jwt token";

	@Autowired
	WebApplicationContext context;

	@MockBean
	CardService cardService;

	@MockBean
	CardSimulationService cardSimulationService;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	DocsFieldGeneratorUtils docsFieldGeneratorUtils;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.alwaysDo(print())
			.apply(documentationConfiguration(restDocumentation))
			.build();
	}

	/**
	 * 요청 성공시 201을 리턴하고 카드 정보를 반환한다.
	 */
	@Nested
	@DisplayName("카드 저장 API 테스트")
	class saveCardTest {

		@Test
		@WithMockMember
		@DisplayName("요청 성공시 201을 리턴하고 카드 정보를 반환한다")
		void shouldReturn201WhenEnrollSuccessTest() throws Exception {
			// given
			SaveCardRequestDto saveCardRequestDto = SaveCardRequestDto.builder()
				.title(new Title("title"))
				.question(new Question("hello"))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.answer(OxAnswer.O.toString())
				.description(new Description("hello"))
				.build();
			Mockito.doReturn(makeResponse()).when(cardService).saveCard(any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/cards").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(saveCardRequestDto))
				.header("Authorization", jwtToken));

			// then
			resultActions.andExpect(status().isCreated())
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
				.andExpect(jsonPath("answer").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("jwt token")),
					docsFieldGeneratorUtils.generateRequestFieldSnippet(
						new ParameterizedTypeReference<SaveCardRequestDto>() {
						}, "card", Locale.KOREAN),
					docsFieldGeneratorUtils.generateResponseFieldSnippet(new ParameterizedTypeReference<CardDto>() {
					}, "card", Locale.KOREAN)));
		}

		private CardDto makeResponse() {
			return CardDto.builder()
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
	 * 성공시 200 응답코드와 리턴해야할 값 검증
	 */
	@Nested
	@DisplayName("카드 업데이트 API 테스트")
	class UpdateCardTest {

		@Test
		@WithMockMember
		@DisplayName("성공시 200 응답코드와 리턴해야할 값 검증")
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			UpdateCardRequestDto requestDto = UpdateCardRequestDto.builder()
				.title(new Title("title"))
				.question(new Question("question"))
				.description(new Description("editText"))
				.categoryId(Id.generateNextId())
				.answer("O")
				.build();
			Mockito.doReturn(makeResponse()).when(cardService).updateCard(any(), any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				put("/api/v1/cards/{cardId}", Id.generateNextId().toString()).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
					.header("Authorization", jwtToken));

			// then
			resultActions.andExpect(status().isOk())
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
				.andExpect(jsonPath("answer").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("jwt token")),
					pathParameters(parameterWithName("cardId").description("카드 아이디")),
					docsFieldGeneratorUtils.generateRequestFieldSnippet(
						new ParameterizedTypeReference<UpdateCardRequestDto>() {
						}, "card", Locale.KOREA),
					docsFieldGeneratorUtils.generateResponseFieldSnippet(new ParameterizedTypeReference<CardDto>() {
					}, "card", Locale.KOREAN)));
		}

		private CardDto makeResponse() {
			return CardDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.memberId(Id.generateNextId())
				.question(new Question("hello"))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.answer(OxAnswer.O.name())
				.description(new Description("hello"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now().plusMinutes(20L))
				.build();
		}
	}

	/**
	 * 정상 동작시 200 응답 테스트
	 */
	@Nested
	@DisplayName("카드 삭제 API")
	class DeleteCardTest {

		@Test
		@DisplayName("정상 동작시 200 응답 테스트")
		@WithMockMember
		void shouldReturnOkWhenCallSuccessTest() throws Exception {
			// given
			Id cardId = Id.generateNextId();

			// when
			ResultActions resultActions = mockMvc.perform(
				delete("/api/v1/cards/{cardId}", cardId).header("Authorization", jwtToken));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("jwt token")),
					pathParameters(parameterWithName("cardId").description("카드 아이디"))));
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
			// given
			CardDto responseDto = CardDto.builder()
				.cardId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.categoryId(Id.generateNextId())
				.question(new Question("question"))
				.cardType(CardType.OX_QUIZ)
				.answer("O")
				.description(new Description("hello"))
				.title(new Title("title1"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
			Mockito.doReturn(responseDto).when(cardService).findCardById(any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/cards/{cardId}/me", Id.generateNextId()).header("Authorization", jwtToken));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("jwt token")),
					pathParameters(parameterWithName("cardId").description("카드 아이디")),
					docsFieldGeneratorUtils.generateResponseFieldSnippet(
						new ParameterizedTypeReference<CardResponseDto>() {
						}, "card", Locale.KOREAN)));

		}
	}

	@Nested
	@DisplayName("공유 카드 단일 조회 API")
	class FindSharedCardTest {

		@Test
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OkAndResponseFormatTest() throws Exception {
			// given
			OxCard card = OxCard.builder()
				.cardId(Id.generateNextId())
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.title(new Title("title"))
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.description(new Description("description"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
			Member member = Member.builder()
				.memberId(Id.generateNextId())
				.email(new Email("abc@gamil.com"))
				.name(Name.of("nickname"))
				.oAuthType(OAuthType.KAKAO)
				.oauthId("id")
				.role(Role.USER)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
			Mockito.doReturn(new CardResponseDto(card, member)).when(cardService).findSharedCard(any());

			// when
			mockMvc.perform(get("/api/v1/cards/{cardId}/share", Id.generateNextId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.card.cardId").exists())
				.andExpect(jsonPath("$.card.categoryId").exists())
				.andExpect(jsonPath("$.card.title").exists())
				.andExpect(jsonPath("$.card.question").exists())
				.andExpect(jsonPath("$.card.cardType").exists())
				.andExpect(jsonPath("$.card.answer").exists())
				.andExpect(jsonPath("$.card.description").exists())
				.andExpect(jsonPath("$.card.title").exists())
				.andExpect(jsonPath("$.member.memberId").exists())
				.andExpect(jsonPath("$.member.name").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					pathParameters(parameterWithName("cardId").description("카드 아이디")),
					responseFields(
						fieldWithPath("card.cardId").description("카드 아이디"),
						fieldWithPath("card.title").description("카드 제목"),
						fieldWithPath("card.memberId").description("카드 멤버 아이디"),
						fieldWithPath("card.question").description("카드 질문"),
						fieldWithPath("card.categoryId").description("카테고리 아이디"),
						fieldWithPath("card.cardType").description("카드 타입"),
						fieldWithPath("card.answer").description("카드 정답"),
						fieldWithPath("card.description").description("카드 설명"),
						fieldWithPath("card.createdAt").description("카드 생성일"),
						fieldWithPath("card.modifiedAt").description("카드 수정일"),
						fieldWithPath("member.memberId").description("멤버 아이디"),
						fieldWithPath("member.name").description("멤버 이름"),
						fieldWithPath("member.profile").description("멤버 profile 이미지"))));
		}
	}

	/**
	 * 성공시 200 응답코드와 리턴해야할 값 검증
	 */
	@Nested
	@DisplayName("개인 카드 커서 페이징 조회 API")
	class CardCursorPagingSearchTest {

		@Test
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			List<CardWithStatisticsDto> contents = List.of(makeResponse());
			CursorPage<CardWithStatisticsDto> cursor = CursorPage.<CardWithStatisticsDto>builder()
				.lastIdExtractStrategy(cardWithStatisticsDto -> cardWithStatisticsDto.getCard().getCardId())
				.contents(contents)
				.pageSize(2)
				.sortOrder(SortOrder.DESC)
				.build();
			CardCursorPageWithCategory cardCursorPageWithCategory = new CardCursorPageWithCategory(cursor);
			cardCursorPageWithCategory.setCategory(Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(new Id("2825b9a9-d89a-4301-99b5-a7668a5b5fff"))
				.thumbnail(Image.of("thumbnail"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.isShared(true)
				.title(new com.almondia.meca.category.domain.vo.Title("title"))
				.build());
			cardCursorPageWithCategory.setMember(Member.builder()
				.memberId(new Id("2825b9a9-d89a-4301-99b5-a7668a5b5fff"))
				.email(new Email("helloworld@naver.com"))
				.name(Name.of("hello"))
				.oAuthType(OAuthType.GOOGLE)
				.role(Role.USER)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build());
			Mockito.doReturn(cardCursorPageWithCategory)
				.when(cardService)
				.searchCursorPagingCard(anyInt(), any(), any(), any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/cards/categories/{categoryId}/me", Id.generateNextId()).header("Authorization", jwtToken)
					.queryParam("hasNext", Id.generateNextId().toString())
					.queryParam("pageSize", "5")
					.queryParam("containTitle", "title"));

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("contents").exists())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andExpect(jsonPath("category").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("jwt 토큰")),
					pathParameters(parameterWithName("categoryId").description("카테고리 아이디")),
					requestParameters(parameterWithName("hasNext").description("다음 페이지가 있는지 여부").optional(),
						parameterWithName("pageSize").description("페이지 사이즈"),
						parameterWithName("containTitle").description("카드 제목 포함 여부").optional()),
					responseFields(fieldWithPath("contents[].card.cardId").description("카드 아이디"),
						fieldWithPath("contents[].card.title").description("카드 제목"),
						fieldWithPath("contents[].card.memberId").description("카드 멤버 아이디"),
						fieldWithPath("contents[].card.question").description("카드 질문"),
						fieldWithPath("contents[].card.categoryId").description("카테고리 아이디"),
						fieldWithPath("contents[].card.cardType").description("카드 타입"),
						fieldWithPath("contents[].card.answer").description("카드 정답"),
						fieldWithPath("contents[].card.description").description("카드 설명"),
						fieldWithPath("contents[].card.createdAt").description("카드 생성일"),
						fieldWithPath("contents[].card.modifiedAt").description("카드 수정일"),
						fieldWithPath("contents[].statistics.scoreAvg").description("평균 점수"),
						fieldWithPath("contents[].statistics.tryCount").description("정답 수"),
						fieldWithPath("pageSize").description("페이지 사이즈"),
						fieldWithPath("sortOrder").description("정렬 방식"),
						fieldWithPath("hasNext").description("다음 페이지가 있는지 여부"),
						fieldWithPath("categoryLikeCount").description("카테고리 좋아요 수"),
						fieldWithPath("category.categoryId").description("카테고리 아이디"),
						fieldWithPath("category.title").description("카테고리 제목"),
						fieldWithPath("category.thumbnail").description("카테고리 썸네일"),
						fieldWithPath("category.shared").description("카테고리 공유 여부"),
						fieldWithPath("category.createdAt").description("카테고리 생성일"),
						fieldWithPath("category.modifiedAt").description("카테고리 수정일"),
						fieldWithPath("category.deleted").description("카테고리 삭제 여부"),
						fieldWithPath("category.memberId").description("카테고리 멤버 아이디"),
						fieldWithPath("member.memberId").description("멤버 아이디"),
						fieldWithPath("member.name").description("멤버 이름"),
						fieldWithPath("member.profile").description("멤버 프로필"))));
		}

		private CardWithStatisticsDto makeResponse() {
			Card card = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
			CardDto cardDto = CardMapper.cardToDto(card);
			return CardWithStatisticsDto.builder().card(cardDto).statistics(new CardStatisticsDto(0.0, 0L)).build();
		}
	}

	@Nested
	@DisplayName("공유 카테고리의 카드 커서 페이징 조회 API")
	class SharedCardCursorPagingTest {

		@Test
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OkAndResponseFormatTest() throws Exception {
			// given
			List<CardWithStatisticsDto> contents = List.of(makeResponse());
			CursorPage<CardWithStatisticsDto> cursor = CursorPage.<CardWithStatisticsDto>builder()
				.lastIdExtractStrategy(cardWithStatisticsDto -> cardWithStatisticsDto.getCard().getCardId())
				.contents(contents)
				.pageSize(2)
				.sortOrder(SortOrder.DESC)
				.build();
			CardCursorPageWithCategory cardCursorPageWithCategory = new CardCursorPageWithCategory(cursor);
			cardCursorPageWithCategory.setCategory(Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(new Id("2825b9a9-d89a-4301-99b5-a7668a5b5fff"))
				.thumbnail(Image.of("thumbnail"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.isShared(true)
				.title(new com.almondia.meca.category.domain.vo.Title("title"))
				.build());
			cardCursorPageWithCategory.setMember(Member.builder()
				.memberId(new Id("2825b9a9-d89a-4301-99b5-a7668a5b5fff"))
				.email(new Email("helloworld@naver.com"))
				.name(Name.of("hello"))
				.oAuthType(OAuthType.GOOGLE)
				.role(Role.USER)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build());
			Mockito.doReturn(cardCursorPageWithCategory)
				.when(cardService)
				.searchCursorPagingSharedCard(anyInt(), any(), any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/cards/categories/{categoryId}/share", Id.generateNextId()).queryParam("hasNext",
					Id.generateNextId().toString()).queryParam("pageSize", "5").queryParam("containTitle", "title"));

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("contents").exists())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andExpect(jsonPath("category").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					pathParameters(parameterWithName("categoryId").description("카테고리 아이디")),
					requestParameters(parameterWithName("hasNext").description("다음 페이지가 있는지 여부").optional(),
						parameterWithName("pageSize").description("페이지 사이즈"),
						parameterWithName("containTitle").description("카드 제목 포함 여부").optional()),
					responseFields(fieldWithPath("contents[].card.cardId").description("카드 아이디"),
						fieldWithPath("contents[].card.title").description("카드 제목"),
						fieldWithPath("contents[].card.memberId").description("카드 멤버 아이디"),
						fieldWithPath("contents[].card.question").description("카드 질문"),
						fieldWithPath("contents[].card.categoryId").description("카테고리 아이디"),
						fieldWithPath("contents[].card.cardType").description("카드 타입"),
						fieldWithPath("contents[].card.answer").description("카드 정답"),
						fieldWithPath("contents[].card.description").description("카드 설명"),
						fieldWithPath("contents[].card.createdAt").description("카드 생성일"),
						fieldWithPath("contents[].card.modifiedAt").description("카드 수정일"),
						fieldWithPath("contents[].statistics.scoreAvg").description("평균 점수"),
						fieldWithPath("contents[].statistics.tryCount").description("정답 수"),
						fieldWithPath("pageSize").description("페이지 사이즈"),
						fieldWithPath("sortOrder").description("정렬 방식"),
						fieldWithPath("hasNext").description("다음 페이지가 있는지 여부"),
						fieldWithPath("categoryLikeCount").description("카테고리 좋아요 수"),
						fieldWithPath("category.categoryId").description("카테고리 아이디"),
						fieldWithPath("category.title").description("카테고리 제목"),
						fieldWithPath("category.thumbnail").description("카테고리 썸네일"),
						fieldWithPath("category.shared").description("카테고리 공유 여부"),
						fieldWithPath("category.createdAt").description("카테고리 생성일"),
						fieldWithPath("category.modifiedAt").description("카테고리 수정일"),
						fieldWithPath("category.deleted").description("카테고리 삭제 여부"),
						fieldWithPath("category.memberId").description("카테고리 멤버 아이디"),
						fieldWithPath("member.memberId").description("멤버 아이디"),
						fieldWithPath("member.name").description("멤버 이름"),
						fieldWithPath("member.profile").description("멤버 프로필"))));
		}

		private CardWithStatisticsDto makeResponse() {
			Card card = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
			CardDto cardDto = CardMapper.cardToDto(card);
			return CardWithStatisticsDto.builder().card(cardDto).statistics(new CardStatisticsDto(null, null)).build();
		}
	}

	/**
	 * 정상 동작시 200 응답 및 응답 포맷 테스트
	 */
	@Nested
	@DisplayName("카드 시뮬레이션 테스트")
	class SimulateCardTest {

		@Test
		@WithMockMember
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			CardDto responseDto = CardDto.builder()
				.cardId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.categoryId(Id.generateNextId())
				.question(new Question("question"))
				.cardType(CardType.OX_QUIZ)
				.answer("O")
				.description(new Description("hello"))
				.title(new Title("title1"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
			Mockito.doReturn(List.of(responseDto)).when(cardSimulationService).simulateRandom(any(), any(), anyInt());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/cards/categories/{categoryId}/simulation", Id.generateNextId()).header("Authorization",
					jwtToken).queryParam("limit", "3").queryParam("algorithm", "random"));

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cardId").exists())
				.andExpect(jsonPath("$[0].memberId").exists())
				.andExpect(jsonPath("$[0].question").exists())
				.andExpect(jsonPath("$[0].cardType").exists())
				.andExpect(jsonPath("$[0].answer").exists())
				.andExpect(jsonPath("$[0].description").exists())
				.andExpect(jsonPath("$[0].title").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("JWT 인증 토큰")),
					pathParameters(parameterWithName("categoryId").description("카테고리 아이디")),
					requestParameters(parameterWithName("limit").description("카드 갯수"),
						parameterWithName("algorithm").description("카드 알고리즘(random, score")),
					responseFields(fieldWithPath("[].cardId").description("카드 아이디"),
						fieldWithPath("[].memberId").description("멤버 아이디"),
						fieldWithPath("[].categoryId").description("카테고리 아이디"),
						fieldWithPath("[].question").description("카드 질문"),
						fieldWithPath("[].cardType").description("카드 타입"),
						fieldWithPath("[].answer").description("카드 정답"),
						fieldWithPath("[].description").description("카드 설명"),
						fieldWithPath("[].title").description("카드 제목"),
						fieldWithPath("[].createdAt").description("카드 생성일"),
						fieldWithPath("[].modifiedAt").description("카드 수정일"))));
		}
	}

	@Nested
	@DisplayName("시뮬레이션 이전에 조회할 카드 갯수 API")
	class FindCardCountByCategoryIdTest {

		@Test
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OKAndResponseFormatTest() throws Exception {
			// given
			List<CardCountGroupByScoreDto> data = List.of(new CardCountGroupByScoreDto(25.0, 1),
				new CardCountGroupByScoreDto(100.0, 4));
			Id categoryId = Id.generateNextId();
			Mockito.doReturn(data).when(cardSimulationService).findCardCountByScore(any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/cards/categories/{categoryId}/simulation/before/count", categoryId));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					pathParameters(parameterWithName("categoryId").description("카테고리 아이디")),
					responseFields(fieldWithPath("[].count").description("카드 갯수"),
						fieldWithPath("[].score").description("카드 점수"))));
		}
	}

	@Nested
	@DisplayName("카테고리 별 카드 갯수 조회 API")
	class FindCardCountByCategoryTest {

		@Test
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		@WithMockMember
		void shouldReturn200OKAndResponseFormatTest() throws Exception {
			// given
			Mockito.doReturn(1L).when(cardService).findCardsCountByCategoryId(any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/cards/categories/{categoryId}/me/count", Id.generateNextId())
					.header("Authorization", jwtToken));

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("count").exists())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("JWT 인증 토큰")),
					pathParameters(parameterWithName("categoryId").description("카테고리 아이디")),
					responseFields(fieldWithPath("count").description("카드 갯수"))));
		}
	}
}