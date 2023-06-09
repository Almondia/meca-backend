package com.almondia.meca.cardhistory.controller;

import static com.almondia.meca.asciidocs.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
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

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.application.CardHistoryService;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardHistoryController2.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({JacksonConfiguration.class})
class CardHistoryController2Test {

	private static final String jwtToken = "jwt token";

	@MockBean
	CardHistoryService cardHistoryService;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	WebApplicationContext context;

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

	@Nested
	@DisplayName("카드 ID 기반 카드 히스토리 조회 API")
	class FindCardHistoriesByCardIdTest {

		@Test
		@DisplayName("정상 응답 테스트")
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			Mockito.doReturn(CursorPage.<CardHistoryWithCardAndMemberResponseDto>builder()
				.contents(List.of(generateCardHistoryWithCardAndMemberResponseDto()))
				.hasNext(null)
				.pageSize(2)
				.sortOrder(SortOrder.DESC)
				.build()).when(cardHistoryService).findCardHistoriesByCardId(any(), anyInt(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v2/histories/cards/{cardId}",
					Id.generateNextId().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.queryParam("pageSize", "2")
					.queryParam("hasNext", Id.generateNextId().toString()));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestParameters(
						parameterWithName("pageSize").description("페이지 사이즈"),
						parameterWithName("hasNext").description("다음 페이지 존재 여부").optional()
					),
					pathParameters(parameterWithName("cardId").description("카드 ID")),
					responseFields(
						fieldWithPath("contents[].cardHistory.cardHistoryId").description("카드 히스토리 ID"),
						fieldWithPath("contents[].cardHistory.userAnswer").description("사용자 답안"),
						fieldWithPath("contents[].cardHistory.score").description("점수"),
						fieldWithPath("contents[].cardHistory.createdAt").description("생성일"),
						fieldWithPath("contents[].solvedMember.solvedMemberId").description("문제를 푼 사용자 ID"),
						fieldWithPath("contents[].solvedMember.solvedMemberName").description("문제를 푼 사용자 이름"),
						fieldWithPath("contents[].card.cardId").description("카드 ID"),
						fieldWithPath("contents[].card.title").description("카드 제목"),
						fieldWithPath("contents[].card.question").description("카드 질문"),
						fieldWithPath("contents[].card.cardType").description("카드 타입"),
						fieldWithPath("contents[].card.answer").description("카드 정답"),
						fieldWithPath("contents[].card.description").description("카드 설명"),
						fieldWithPath("contents[].card.createdAt").description("카드 생성일"),
						fieldWithPath("contents[].card.modifiedAt").description("카드 수정일"),
						fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("pageSize").description("페이지 사이즈"),
						fieldWithPath("sortOrder").description("정렬 방식")
					)
				));
		}
	}

	@Nested
	@DisplayName("카테고리 기반 카드 히스토리 조회 API")
	class FindCardHistoryByCategoryIdTest {

		@Test
		@DisplayName("정상 응답 테스트")
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			Mockito.doReturn(CursorPage.<CardHistoryWithCardAndMemberResponseDto>builder()
				.contents(List.of(generateCardHistoryWithCardAndMemberResponseDto()))
				.hasNext(null)
				.pageSize(2)
				.sortOrder(SortOrder.DESC)
				.build()).when(cardHistoryService).findCardHistoriesByCategoryId(any(), anyInt(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v2/histories/categories/{categoryId}", Id.generateNextId().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.queryParam("hasNext", Id.generateNextId().toString())
					.queryParam("pageSize", "2"));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestParameters(parameterWithName("hasNext").description("다음 페이지 존재 여부").optional(),
						parameterWithName("pageSize").description("페이지 사이즈")
					),
					pathParameters(parameterWithName("categoryId").description("카테고리 ID")),
					responseFields(
						fieldWithPath("contents[].cardHistory.cardHistoryId").description("카드 히스토리 ID"),
						fieldWithPath("contents[].cardHistory.userAnswer").description("사용자 답안"),
						fieldWithPath("contents[].cardHistory.score").description("점수"),
						fieldWithPath("contents[].cardHistory.createdAt").description("생성일"),
						fieldWithPath("contents[].solvedMember.solvedMemberId").description("문제를 푼 사용자 ID"),
						fieldWithPath("contents[].solvedMember.solvedMemberName").description("문제를 푼 사용자 이름"),
						fieldWithPath("contents[].card.cardId").description("카드 ID"),
						fieldWithPath("contents[].card.title").description("카드 제목"),
						fieldWithPath("contents[].card.memberId").description("카드 작성자 ID"),
						fieldWithPath("contents[].card.question").description("카드 질문"),
						fieldWithPath("contents[].card.categoryId").description("카테고리 ID"),
						fieldWithPath("contents[].card.cardType").description("카드 타입"),
						fieldWithPath("contents[].card.createdAt").description("카드 생성일"),
						fieldWithPath("contents[].card.modifiedAt").description("카드 수정일"),
						fieldWithPath("contents[].card.answer").description("카드 정답"),
						fieldWithPath("contents[].card.description").description("카드 설명"),
						fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("pageSize").description("페이지 사이즈"),
						fieldWithPath("sortOrder").description("정렬 방식")
					)
				));
		}
	}

	@Nested
	@DisplayName("푼 사용자 기반 카드 히스토리 조회 API")
	class FindCardHistoriesBySolvedMemberIdTest2 {

		@Test
		@DisplayName("정상 응답 테스트")
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			Mockito.doReturn(CursorPage.<CardHistoryWithCardAndMemberResponseDto>builder()
				.contents(List.of(generateCardHistoryWithCardAndMemberResponseDto()))
				.hasNext(null)
				.pageSize(2)
				.sortOrder(SortOrder.DESC)
				.build()).when(cardHistoryService).findCardHistoriesBySolvedMemberId(any(), anyInt(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v2/histories/members/{solvedMemberId}", Id.generateNextId().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.queryParam("hasNext", Id.generateNextId().toString())
					.queryParam("pageSize", "2"));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestParameters(parameterWithName("hasNext").description("다음 페이지 존재 여부").optional(),
						parameterWithName("pageSize").description("페이지 사이즈")
					),
					pathParameters(parameterWithName("solvedMemberId").description("문제를 푼 사용자 ID")),
					responseFields(
						fieldWithPath("contents[].cardHistory.cardHistoryId").description("카드 히스토리 ID"),
						fieldWithPath("contents[].cardHistory.userAnswer").description("사용자 답안"),
						fieldWithPath("contents[].cardHistory.score").description("점수"),
						fieldWithPath("contents[].cardHistory.createdAt").description("생성일"),
						fieldWithPath("contents[].solvedMember.solvedMemberId").description("문제를 푼 사용자 ID"),
						fieldWithPath("contents[].solvedMember.solvedMemberName").description("문제를 푼 사용자 이름"),
						fieldWithPath("contents[].card.cardId").description("카드 ID"),
						fieldWithPath("contents[].card.title").description("카드 제목"),
						fieldWithPath("contents[].card.question").description("카드 질문"),
						fieldWithPath("contents[].card.cardType").description("카드 타입"),
						fieldWithPath("contents[].card.answer").description("카드 정답"),
						fieldWithPath("contents[].card.description").description("카드 설명"),
						fieldWithPath("contents[].card.createdAt").description("카드 생성일"),
						fieldWithPath("contents[].card.modifiedAt").description("카드 수정일"),
						fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("pageSize").description("페이지 사이즈"),
						fieldWithPath("sortOrder").description("정렬 방식")
					)
				));
		}
	}

	CardHistoryWithCardAndMemberResponseDto generateCardHistoryWithCardAndMemberResponseDto() {
		final Id solvedMemberId = Id.generateNextId();
		final Id cardId = Id.generateNextId();
		final Id categoryId = Id.generateNextId();
		CardHistory cardHistory = CardHistoryTestHelper.generateCardHistory(cardId, solvedMemberId);
		Card card = CardTestHelper.genOxCard(solvedMemberId, categoryId, cardId);
		return new CardHistoryWithCardAndMemberResponseDto(cardHistory, card.getCardId(), solvedMemberId,
			Name.of("simon"), CardSnapShot.copyShot(card));
	}
}