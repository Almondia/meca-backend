package com.almondia.meca.card.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Image;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.card.sevice.CardService;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({JacksonConfiguration.class})
class CardControllerTest {

	@MockBean
	CardService cardService;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

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
				.images("A,B,C,D")
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.answer(OxAnswer.O.toString())
				.build();
			Mockito.doReturn(makeResponse()).when(cardService).saveCard(any(), any());

			mockMvc.perform(post("/api/v1/cards")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(saveCardRequestDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("card_id").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("question").exists())
				.andExpect(jsonPath("images").exists())
				.andExpect(jsonPath("category_id").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("card_type").exists())
				.andExpect(jsonPath("created_at").exists())
				.andExpect(jsonPath("modified_at").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("ox_answer").exists());
		}

		private CardResponseDto makeResponse() {
			return CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.question(new Question("hello"))
				.images(List.of(new Image("A"), new Image("B"), new Image("C")))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.isDeleted(false)
				.oxAnswer(OxAnswer.O)
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
				.cardType(CardType.OX_QUIZ)
				.question(new Question("question"))
				.images("A,B,C")
				.categoryId(Id.generateNextId())
				.build();
			Mockito.doReturn(makeResponse())
				.when(cardService).updateCard(any(), any(), any());

			mockMvc.perform(put("/api/v1/cards/{cardId}", Id.generateNextId().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("card_id").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("question").exists())
				.andExpect(jsonPath("images").exists())
				.andExpect(jsonPath("category_id").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("card_type").exists())
				.andExpect(jsonPath("created_at").exists())
				.andExpect(jsonPath("modified_at").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("ox_answer").exists());
		}

		private CardResponseDto makeResponse() {
			return CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.question(new Question("hello"))
				.images(List.of(new Image("A"), new Image("B"), new Image("C")))
				.categoryId(Id.generateNextId())
				.cardType(CardType.OX_QUIZ)
				.isDeleted(false)
				.oxAnswer(OxAnswer.O)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build();
		}
	}
}