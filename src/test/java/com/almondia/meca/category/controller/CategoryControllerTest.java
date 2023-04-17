package com.almondia.meca.category.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import com.almondia.meca.category.application.CategoryService;
import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.configuration.web.WebMvcConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({WebMvcConfiguration.class, JacksonConfiguration.class})
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
				.thumbnail(new Image("https://aws.s3.com"))
				.title(Title.of("title"))
				.isDeleted(false)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build()).when(categoryservice).saveCategory(any(), any());
			mockMvc.perform(post("/api/v1/categories")
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(makeCategoryRequestDto()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("categoryId").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("thumbnail").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("shared").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists());
		}

		private String makeCategoryRequestDto() throws JsonProcessingException {
			SaveCategoryRequestDto requestDto = SaveCategoryRequestDto.builder().title(Title.of("title"))
				.thumbnail(new Image("https://aws.s3.com")).build();
			return objectMapper.writeValueAsString(requestDto);
		}
	}

	/**
	 * 1. 카테 고리 수정 응답 코드 200 및 정상 응답 테스트
	 * 2. 권한 오류가 발생시 403응답 반환
	 */
	@Nested
	@DisplayName("카테고리 수정")
	class UpdateCategoryTest {

		@Test
		@WithMockMember
		@DisplayName("카테 고리 수정 응답 코드 200 및 정상 응답 테스트")
		void shouldReturn200WhenSuccessUpdateCategoryTest() throws Exception {
			Mockito.doReturn(CategoryResponseDto.builder()
					.categoryId(Id.generateNextId())
					.memberId(Id.generateNextId())
					.title(new Title("title"))
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.build())
				.when(categoryservice).updateCategory(any(), any(), any());
			mockMvc.perform(put("/api/v1/categories/{categoryId}", Id.generateNextId())
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(makeCategoryRequestDto()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("categoryId").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("shared").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists());
		}

		@Test
		@WithMockMember
		@DisplayName("권한 오류가 발생시 403 응답 반환")
		void shouldThrowWhenAuthorizationErrorTest() throws Exception {
			Mockito.doThrow(new AccessDeniedException("권한 없음"))
				.when(categoryservice).updateCategory(any(), any(), any());
			mockMvc.perform(put("/api/v1/categories/{categoryId}", Id.generateNextId())
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(makeCategoryRequestDto()))
				.andExpect(status().isForbidden());
		}

		private String makeCategoryRequestDto() throws JsonProcessingException {
			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(new Title("title"))
				.build();
			return objectMapper.writeValueAsString(updateCategoryRequestDto);
		}
	}

	@Nested
	@DisplayName("커서 페이징 카테고리 조회 테스트")
	class OffsetPagingCategoryTest {

		@Test
		@DisplayName("카테 고리 페이징 조회 응답 코드 200 및 정상 응답 테스트")
		@WithMockMember
		void shouldReturnPageTypeWhenCallPagingSearchTest() throws Exception {
			CategoryWithHistoryResponseDto content = CategoryWithHistoryResponseDto.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.title(new Title("title"))
				.thumbnail(Image.of("https://aws.s3.com"))
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.scoreAvg(12.3)
				.solveCount(10L)
				.totalCount(20L)
				.build();
			CursorPage<CategoryWithHistoryResponseDto> response = CursorPage.<CategoryWithHistoryResponseDto>builder()
				.contents(List.of(content))
				.pageSize(1).hasNext(Id.generateNextId())
				.sortOrder(SortOrder.DESC)
				.build();
			Mockito.doReturn(response)
				.when(categoryservice).findCursorPagingCategoryWithHistoryResponse(anyInt(), any(), any(), any());

			final String url = "/api/v1/categories/me?pageSize=4&sortField=createdAt&startCreatedAt=2023-03-13T10:23";
			mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("hasNext").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("카테고리 삭제")
	class DeleteCategoryTest {

		@Test
		@WithMockMember
		@DisplayName("카테고리 삭제 성공시 응답 200")
		void shouldReturnStatus200WhenSuccessTest() throws Exception {
			mockMvc.perform(delete("/api/v1/categories/{categoryId}", Id.generateNextId()))
				.andExpect(status().isOk());
		}
	}

	@Nested
	@DisplayName("카테고리 공유 커서 페이징")
	class SearchShareCategoryTest {

		@Test
		@DisplayName("카테고리 공유 커서 페이징 성공시 응답 200 및 정상 응답")
		void shouldReturnStatus200AndResponseWhenSuccessTest() throws Exception {
			CursorPage<SharedCategoryResponseDto> cursorPage = CursorPage.<SharedCategoryResponseDto>builder()
				.pageSize(2)
				.contents(List.of(new SharedCategoryResponseDto(
					CategoryTestHelper.generateSharedCategory("title", Id.generateNextId(), Id.generateNextId()),
					MemberTestHelper.generateMember(Id.generateNextId()))))
				.hasNext(Id.generateNextId())
				.sortOrder(SortOrder.DESC)
				.build();
			Mockito.doReturn(cursorPage).when(categoryservice).findCursorPagingCategoryResponseDto(anyInt(), any());
			mockMvc.perform(get("/api/v1/categories/share?pageSize=2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("hasNext").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andExpect(jsonPath("contents").exists())
				.andDo(print());
		}
	}
}