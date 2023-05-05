package com.almondia.meca.cardhistory.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
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

import com.almondia.meca.cardhistory.application.CardHistoryService;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardHistoryTestHelper;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardHistoryController.class)
@Import({JacksonConfiguration.class})
class CardHistoryControllerTest {

	@MockBean
	CardHistoryService cardHistoryService;

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
	 * 정상 응답 테스트
	 */
	@Nested
	@DisplayName("시뮬레이션 결과 저장 API")
	class SaveSimulationCardHistoryTest {

		@Test
		@DisplayName("정상 응답 테스트")
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			CardHistoryDto historyDto = CardHistoryDto.builder()
				.cardId(Id.generateNextId())
				.userAnswer(new Answer("answer"))
				.score(new Score(100))
				.build();
			SaveRequestCardHistoryDto saveRequestCardHistoryDto = new SaveRequestCardHistoryDto(List.of(historyDto));

			mockMvc.perform(post("/api/v1/histories/simulation")
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(objectMapper.writeValueAsString(saveRequestCardHistoryDto)))
				.andExpect(status().isCreated());
		}
	}

	/**
	 * 정상 응답 테스트
	 */
	@Nested
	@DisplayName("시뮬레이션 결과 조회 API")
	class FindSimulationCardHistoryTest {

		@Test
		@DisplayName("정상 응답 테스트")
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			Mockito.doReturn(CursorPage.builder()
					.contents(List.of(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), Id.generateNextId(),
						Id.generateNextId(), 100)))
					.hasNext(null)
					.pageSize(2)
					.build())
				.when(cardHistoryService).findCardHistoriesByCardId(any(), anyInt(), any());
			mockMvc.perform(get("/api/v1/histories/cards/{cardId}?pageSize=2", Id.generateNextId().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isOk());
		}
	}

	@Nested
	@DisplayName("카테고리 기반 카드 히스토리 조회 API")
	class FindCardHistoryByCategoryIdTest {

		@Test
		@DisplayName("정상 응답 테스트")
		@WithMockMember
		void shouldReturn200WhenSuccessTest() throws Exception {
			Mockito.doReturn(CursorPage.builder()
					.contents(List.of(CardHistoryTestHelper.generateCardHistory(Id.generateNextId(), Id.generateNextId(),
						Id.generateNextId(), 100)))
					.hasNext(null)
					.pageSize(2)
					.build())
				.when(cardHistoryService).findCardHistoriesByCategoryId(any(), anyInt(), any());
			mockMvc.perform(get("/api/v1/histories/categories/{categoryId}?pageSize=2", Id.generateNextId().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpect(status().isOk());
		}
	}
}