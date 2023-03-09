package com.almondia.meca.category.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.category.service.CategoryService;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({JacksonConfiguration.class})
class CategoryControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	CategoryService categoryservice;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	ObjectMapper objectMapper;

	/**
	 * 1. 카테고리 등록시 성공하면 201 코드 및 응답 검증
	 */
	@Nested
	@DisplayName("카테고리 등록")
	class saveCategoryTest {
		@Test
		@DisplayName("카테고리 등록시 성공하면 201 코드 및 응답 검증")
		@WithMockMember
		void test() throws Exception {
			Mockito.doReturn(CategoryResponseDto
				.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.title(new Title("title"))
				.isDeleted(false)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build()).when(categoryservice).saveCategory(any(), any());
			mockMvc.perform(post("/api/v1/categories")
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(makeCategoryRequestDto()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("category_id").exists())
				.andExpect(jsonPath("member_id").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("shared").exists())
				.andExpect(jsonPath("created_at").exists())
				.andExpect(jsonPath("modified_at").exists());
		}

		private String makeCategoryRequestDto() throws JsonProcessingException {
			SaveCategoryRequestDto requestDto = SaveCategoryRequestDto.builder().title(new Title("title")).build();
			return objectMapper.writeValueAsString(requestDto);
		}
	}

}