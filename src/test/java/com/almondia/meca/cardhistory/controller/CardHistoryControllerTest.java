package com.almondia.meca.cardhistory.controller;

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
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
import com.almondia.meca.cardhistory.domain.vo.Score;
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

@WebMvcTest(CardHistoryController.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({JacksonConfiguration.class})
class CardHistoryControllerTest {

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

	/**
	 * 정상 응답 테스트
	 */
	@Nested
	@DisplayName("시뮬레이션 결과 저장 API")
	class SaveSimulationCardHistoryTest {

		@Test
		@DisplayName("정상 응답 테스트")
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			// given
			CardHistoryRequestDto historyDto = CardHistoryRequestDto.builder()
				.cardId(Id.generateNextId())
				.userAnswer(new Answer("answer"))
				.score(new Score(100))
				.build();
			Id categoryId = Id.generateNextId();
			SaveRequestCardHistoryDto saveRequestCardHistoryDto = new SaveRequestCardHistoryDto(List.of(historyDto));

			// when
			ResultActions resultActions = mockMvc.perform(
				post("/api/v1/histories/simulation").contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(objectMapper.writeValueAsString(saveRequestCardHistoryDto))
					.header("Authorization", "Bearer " + jwtToken));

			// then
			resultActions.andExpect(status().isCreated())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("JWT 토큰")
					),
					requestFields(
						fieldWithPath("cardHistories[].cardId").description("카드 ID"),
						fieldWithPath("cardHistories[].userAnswer").description("사용자 답안")
							.attributes(key("constraints").value("100글자 이내")),
						fieldWithPath("cardHistories[].score").description("점수")
							.attributes(key("constraints").value("0 ~ 100 정수"))
					)
				));
		}
	}

	@Nested
	@DisplayName("카드 히스토리 푼 사용자기반 조회")
	class FindCardHistoriesBySolvedMemberIdTest {

		@Test
		@DisplayName("정상 응답 테스트")
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
				get("/api/v1/histories/members/{solvedMemberId}", Id.generateNextId().toString())
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
						fieldWithPath("contents[].cardHistoryId").description("카드 히스토리 ID"),
						fieldWithPath("contents[].solvedUserId").description("문제를 푼 사용자 ID"),
						fieldWithPath("contents[].solvedUserName").description("문제를 푼 사용자 이름"),
						fieldWithPath("contents[].userAnswer").description("사용자 답안"),
						fieldWithPath("contents[].score").description("점수"),
						fieldWithPath("contents[].categoryId").description("카테고리 ID"),
						fieldWithPath("contents[].cardId").description("카드 ID"),
						fieldWithPath("contents[].memberId").description("카드를 생성한 사용자 ID"),
						fieldWithPath("contents[].cardType").description("카드 타입"),
						fieldWithPath("contents[].question").description("문제"),
						fieldWithPath("contents[].answer").description("정답"),
						fieldWithPath("contents[].title").description("카드 제목"),
						fieldWithPath("contents[].createdAt").description("생성일"),
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