package com.almondia.meca.card.controller;

import static com.almondia.meca.asciidocs.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
@ExtendWith({RestDocumentationExtension.class})
@Import({JacksonConfiguration.class})
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
					requestHeaders(headerWithName("Authorization").description("jwt token")), requestFields(
						fieldWithPath("title").description("카드 제목").attributes(key("constraints").value("2 ~ 40 글자")),
						fieldWithPath("question").description("카드 질문")
							.attributes(key("constraints").value("공백 없이 500 글자 이하")),
						fieldWithPath("description").description("카드 설명")
							.optional()
							.attributes(key("constraints").value("2,1000자 이하 글자")),
						fieldWithPath("categoryId").description("카테고리 아이디"),
						fieldWithPath("cardType").description("카드 타입")
							.attributes(key("constraints").value("OX_QUIZ, CHOICE_QUIZ, SHORT_ANSWER_QUIZ")),
						fieldWithPath("answer").description("카드 정답")),
					responseFields(fieldWithPath("cardId").description("카드 아이디"),
						fieldWithPath("title").description("카드 제목"), fieldWithPath("memberId").description("멤버 아이디"),
						fieldWithPath("question").description("카드 질문"),
						fieldWithPath("description").description("카드 설명"),
						fieldWithPath("categoryId").description("카테고리 아이디"),
						fieldWithPath("cardType").description("카드 타입"),
						fieldWithPath("createdAt").description("카드 생성일"),
						fieldWithPath("modifiedAt").description("카드 수정일"), fieldWithPath("title").description("카드 제목"),
						fieldWithPath("answer").description("카드 정답"))));
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
					pathParameters(parameterWithName("cardId").description("카드 아이디")), requestFields(
						fieldWithPath("title").description("변경할 카드 제목")
							.optional()
							.attributes(key("constraints").value("2 ~ 40 글자")),
						fieldWithPath("question").description("변경할 카드 질문")
							.optional()
							.attributes(key("constraints").value("공백 없이 500 글자 이하")),
						fieldWithPath("description").description("변경할 카드 설명")
							.optional()
							.attributes(key("constraints").value("2,1000자 이하 글자")),
						fieldWithPath("categoryId").description("변경할 카테고리 아이디").optional()),
					responseFields(fieldWithPath("cardId").description("카드 아이디"),
						fieldWithPath("title").description("카드 제목"), fieldWithPath("memberId").description("멤버 아이디"),
						fieldWithPath("question").description("카드 질문"),
						fieldWithPath("description").description("카드 설명"),
						fieldWithPath("categoryId").description("카테고리 아이디"),
						fieldWithPath("cardType").description("카드 타입"),
						fieldWithPath("createdAt").description("카드 생성일"),
						fieldWithPath("modifiedAt").description("카드 수정일"), fieldWithPath("title").description("카드 제목"),
						fieldWithPath("answer").description("카드 정답"))));
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
			CardResponseDto responseDto = CardResponseDto.builder()
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
				.andExpect(jsonPath("cardId").exists())
				.andExpect(jsonPath("question").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("cardType").exists())
				.andExpect(jsonPath("answer").exists())
				.andExpect(jsonPath("description").exists())
				.andExpect(jsonPath("title").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("jwt token")),
					pathParameters(parameterWithName("cardId").description("카드 아이디")),
					responseFields(fieldWithPath("cardId").description("카드 아이디"),
						fieldWithPath("question").description("카드 질문"), fieldWithPath("memberId").description("멤버 아이디"),
						fieldWithPath("cardType").description("카드 타입"), fieldWithPath("answer").description("카드 정답"),
						fieldWithPath("description").description("카드 설명"), fieldWithPath("title").description("카드 제목"),
						fieldWithPath("createdAt").description("카드 생성일"),
						fieldWithPath("modifiedAt").description("카드 수정일"),
						fieldWithPath("categoryId").description("카테고리 아이디"))));
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
				.name(new Name("nickname"))
				.oAuthType(OAuthType.KAKAO)
				.oauthId("id")
				.role(Role.USER)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
			Mockito.doReturn(new SharedCardResponseDto(card, member)).when(cardService).findSharedCard(any());

			// when
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
				.andExpect(jsonPath("$.memberInfo.role").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					pathParameters(parameterWithName("cardId").description("카드 아이디")),
					responseFields(fieldWithPath("cardInfo.cardId").description("카드 아이디"),
						fieldWithPath("cardInfo.title").description("카드 제목"),
						fieldWithPath("cardInfo.memberId").description("카드 멤버 아이디"),
						fieldWithPath("cardInfo.question").description("카드 질문"),
						fieldWithPath("cardInfo.categoryId").description("카테고리 아이디"),
						fieldWithPath("cardInfo.cardType").description("카드 타입"),
						fieldWithPath("cardInfo.answer").description("카드 정답"),
						fieldWithPath("cardInfo.description").description("카드 설명"),
						fieldWithPath("cardInfo.createdAt").description("카드 생성일"),
						fieldWithPath("cardInfo.modifiedAt").description("카드 수정일"),
						fieldWithPath("memberInfo.memberId").description("멤버 아이디"),
						fieldWithPath("memberInfo.name").description("멤버 이름"),
						fieldWithPath("memberInfo.email").description("멤버 이메일"),
						fieldWithPath("memberInfo.profile").description("멤버 profile 이미지"),
						fieldWithPath("memberInfo.role").description("멤버 권한"),
						fieldWithPath("memberInfo.createdAt").description("멤버 생성일"),
						fieldWithPath("memberInfo.modifiedAt").description("멤버 수정일"),
						fieldWithPath("memberInfo.oauthType").description("KAKAO, NAVER, GOOGLE"),
						fieldWithPath("memberInfo.deleted").description("멤버 삭제 여부"))));
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
					responseFields(fieldWithPath("contents[].cardId").description("카드 아이디"),
						fieldWithPath("contents[].title").description("카드 제목"),
						fieldWithPath("contents[].memberId").description("카드 멤버 아이디"),
						fieldWithPath("contents[].question").description("카드 질문"),
						fieldWithPath("contents[].categoryId").description("카테고리 아이디"),
						fieldWithPath("contents[].cardType").description("카드 타입"),
						fieldWithPath("contents[].answer").description("카드 정답"),
						fieldWithPath("contents[].description").description("카드 설명"),
						fieldWithPath("contents[].createdAt").description("카드 생성일"),
						fieldWithPath("contents[].modifiedAt").description("카드 수정일"),
						fieldWithPath("pageSize").description("페이지 사이즈"),
						fieldWithPath("sortOrder").description("정렬 방식"),
						fieldWithPath("hasNext").description("다음 페이지가 있는지 여부"),
						fieldWithPath("category.categoryId").description("카테고리 아이디"),
						fieldWithPath("category.title").description("카테고리 제목"),
						fieldWithPath("category.thumbnail").description("카테고리 썸네일"),
						fieldWithPath("category.shared").description("카테고리 공유 여부"),
						fieldWithPath("category.createdAt").description("카테고리 생성일"),
						fieldWithPath("category.modifiedAt").description("카테고리 수정일"),
						fieldWithPath("category.deleted").description("카테고리 삭제 여부"),
						fieldWithPath("category.memberId").description("카테고리 멤버 아이디"))));
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

	@Nested
	@DisplayName("공유 카테고리의 카드 커서 페이징 조회 API")
	class SharedCardCursorPagingTest {

		@Test
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OkAndResponseFormatTest() throws Exception {
			// given
			List<SharedCardResponseDto> contents = List.of(makeResponse());
			CardCursorPageWithSharedCategoryDto cardCursorPageWithCategory = new CardCursorPageWithSharedCategoryDto(
				contents, Id.generateNextId(), 5, SortOrder.DESC);
			cardCursorPageWithCategory.setCategory(Category.builder()
				.categoryId(Id.generateNextId())
				.title(new com.almondia.meca.category.domain.vo.Title("title"))
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
					responseFields(fieldWithPath("contents[].cardInfo.cardId").description("카드 아이디"),
						fieldWithPath("contents[].cardInfo.title").description("카드 제목"),
						fieldWithPath("contents[].cardInfo.memberId").description("카드 멤버 아이디"),
						fieldWithPath("contents[].cardInfo.question").description("카드 질문"),
						fieldWithPath("contents[].cardInfo.categoryId").description("카테고리 아이디"),
						fieldWithPath("contents[].cardInfo.cardType").description("카드 타입"),
						fieldWithPath("contents[].cardInfo.answer").description("카드 정답"),
						fieldWithPath("contents[].cardInfo.description").description("카드 설명"),
						fieldWithPath("contents[].cardInfo.createdAt").description("카드 생성일"),
						fieldWithPath("contents[].cardInfo.modifiedAt").description("카드 수정일"),
						fieldWithPath("contents[].memberInfo.memberId").description("멤버 아이디"),
						fieldWithPath("contents[].memberInfo.email").description("멤버 이메일"),
						fieldWithPath("contents[].memberInfo.name").description("멤버 이름"),
						fieldWithPath("contents[].memberInfo.profile").description("멤버 프로필 이미지"),
						fieldWithPath("contents[].memberInfo.role").description("멤버 권한"),
						fieldWithPath("contents[].memberInfo.createdAt").description("멤버 생성일"),
						fieldWithPath("contents[].memberInfo.modifiedAt").description("멤버 수정일"),
						fieldWithPath("contents[].memberInfo.deleted").description("멤버 삭제 여부"),
						fieldWithPath("contents[].memberInfo.oauthType").description("KAKAO, GOOGLE, NAVER"),
						fieldWithPath("pageSize").description("페이지 사이즈"),
						fieldWithPath("sortOrder").description("정렬 방식"),
						fieldWithPath("hasNext").description("다음 페이지가 있는지 여부"),
						fieldWithPath("category.categoryId").description("카테고리 아이디"),
						fieldWithPath("category.title").description("카테고리 제목"),
						fieldWithPath("category.thumbnail").description("카테고리 썸네일"),
						fieldWithPath("category.shared").description("카테고리 공유 여부"),
						fieldWithPath("category.createdAt").description("카테고리 생성일"),
						fieldWithPath("category.modifiedAt").description("카테고리 수정일"),
						fieldWithPath("category.deleted").description("카테고리 삭제 여부"),
						fieldWithPath("category.memberId").description("카테고리 멤버 아이디"))));
		}

		private SharedCardResponseDto makeResponse() {
			Card card = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
			Member member = MemberTestHelper.generateMember(Id.generateNextId());
			return new SharedCardResponseDto(card, member);
		}
	}

	/**
	 *  정상 동작시 200 응답 및 응답 포맷 테스트
	 */
	@Nested
	@DisplayName("카드 시뮬레이션 테스트")
	class SimulateCardTest {

		@Test
		@WithMockMember
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			CardResponseDto responseDto = CardResponseDto.builder()
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
				.andDo(document("{class-name}/{method-name}",
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
	@DisplayName("카테고리 별 카드 갯수 조회 API")
	class FindCardCountByCategoryTest {

		@Test
		@WithMockMember
		@DisplayName("정상 동작시 200 응답 및 응답 포맷 테스트")
		void shouldReturn200OKAndResponseFormatTest() throws Exception {
			// given
			Mockito.doReturn(1L).when(cardService).findCardsCountByCategoryId(any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/cards//categories/{categoryId}/me/count", Id.generateNextId())
					.header("Authorization", jwtToken));

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("count").exists())
				.andDo(document("{class-name}/{method-name}",
					requestHeaders(headerWithName("Authorization").description("JWT 인증 토큰")),
					pathParameters(parameterWithName("categoryId").description("카테고리 아이디")),
					responseFields(fieldWithPath("count").description("카드 갯수"))));
		}
	}
}