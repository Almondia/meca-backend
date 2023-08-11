package com.almondia.meca.category.controller;

import static com.almondia.meca.asciidocs.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.almondia.meca.asciidocs.fields.DocsFieldGeneratorUtils;
import com.almondia.meca.category.application.CategoryRecommendService;
import com.almondia.meca.category.application.CategoryService;
import com.almondia.meca.category.controller.dto.CategoryDto;
import com.almondia.meca.category.controller.dto.CategoryStatisticsDto;
import com.almondia.meca.category.controller.dto.CategoryWithStatisticsResponseDto;
import com.almondia.meca.category.controller.dto.SaveCategoryRequestDto;
import com.almondia.meca.category.controller.dto.SharedCategoryResponseDto;
import com.almondia.meca.category.controller.dto.SharedCategoryWithStatisticsAndRecommendDto;
import com.almondia.meca.category.controller.dto.UpdateCategoryRequestDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.configuration.web.WebMvcConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.configuration.asciidocs.DocsFieldGeneratorUtilsConfiguration;
import com.almondia.meca.helper.CategoryTestHelper;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({WebMvcConfiguration.class, JacksonConfiguration.class, DocsFieldGeneratorUtilsConfiguration.class})
class CategoryControllerTest {

	private static final String jwtToken = "jwt token";

	@Autowired
	WebApplicationContext context;

	MockMvc mockMvc;

	@MockBean
	CategoryService categoryservice;

	@MockBean
	CategoryRecommendService categoryRecommendService;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

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
	 * 1. 카테고리 등록시 성공하면 201 코드 및 응답 검증
	 */
	@Nested
	@DisplayName("카테고리 등록")
	class saveCategoryTest {
		@Test
		@DisplayName("카테고리 등록시 성공하면 201 코드 및 응답 검증")
		@WithMockMember
		void shouldReturn201WhenEnrollSuccessTest() throws Exception {
			// given
			Mockito.doReturn(CategoryTestHelper.generateCategoryResponseDto())
				.when(categoryservice)
				.saveCategory(any(), any());
			String saveRequest = objectMapper.writeValueAsString(CategoryTestHelper.generateSaveCategoryRequestDto());

			// when
			ResultActions result = mockMvc.perform(post("/api/v1/categories").header("Authorization", jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(saveRequest));

			//then
			result.andExpect(status().isCreated())
				.andExpect(jsonPath("categoryId").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("thumbnail").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("shared").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					docsFieldGeneratorUtils.generateRequestFieldSnippet(
						new ParameterizedTypeReference<SaveCategoryRequestDto>() {
						}, "category",
						Locale.KOREAN),
					docsFieldGeneratorUtils.generateResponseFieldSnippet(new ParameterizedTypeReference<CategoryDto>() {
																		 }, "category",
						Locale.KOREAN)));
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
			// given
			Mockito.doReturn(CategoryTestHelper.generateCategoryResponseDto())
				.when(categoryservice)
				.updateCategory(any(), any(), any());

			// when
			ResultActions result = mockMvc.perform(
				put("/api/v1/categories/{categoryId}", Id.generateNextId()).contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.header("Authorization", jwtToken)
					.content(makeCategoryRequestDto()));

			// then
			result.andExpect(status().isOk())
				.andExpect(jsonPath("categoryId").exists())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("title").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("shared").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists())
				.andDo(document("{class-name}/{method-name}", getDocumentRequest(), getDocumentResponse(),
					requestHeaders(headerWithName("Authorization").description("JWT Bearer 토큰")),
					pathParameters(parameterWithName("categoryId").description("카테고리 아이디")),
					docsFieldGeneratorUtils.generateRequestFieldSnippet(
						new ParameterizedTypeReference<UpdateCategoryRequestDto>() {
						}, "category",
						Locale.KOREAN),
					docsFieldGeneratorUtils.generateResponseFieldSnippet(new ParameterizedTypeReference<CategoryDto>() {
																		 }, "category",
						Locale.KOREAN)));
		}

		@Test
		@WithMockMember
		@DisplayName("권한 오류가 발생시 403 응답 반환")
		void shouldThrowWhenAuthorizationErrorTest() throws Exception {
			Mockito.doThrow(new AccessDeniedException("권한 없음"))
				.when(categoryservice)
				.updateCategory(any(), any(), any());
			mockMvc.perform(
				put("/api/v1/categories/{categoryId}", Id.generateNextId()).contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(makeCategoryRequestDto())).andExpect(status().isForbidden());
		}

		private String makeCategoryRequestDto() throws JsonProcessingException {
			UpdateCategoryRequestDto updateCategoryRequestDto = UpdateCategoryRequestDto.builder()
				.title(new Title("title"))
				.thumbnail(new Image("https://aws.s3.com"))
				.shared(true)
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
			// given
			Id memberId = Id.generateNextId();
			Id categoryId1 = Id.generateNextId();
			Id categoryId2 = Id.generateNextId();
			CategoryWithStatisticsResponseDto content0 = CategoryWithStatisticsResponseDto.builder()
				.category(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId1))
				.statistics(new CategoryStatisticsDto(12.3, 10L, 20L))
				.build();
			CategoryWithStatisticsResponseDto content1 = CategoryWithStatisticsResponseDto.builder()
				.category(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId2))
				.statistics(new CategoryStatisticsDto(12.3, 10L, 20L))
				.build();
			CursorPage<CategoryWithStatisticsResponseDto> response = CursorPage.<CategoryWithStatisticsResponseDto>builder()
				.lastIdExtractStrategy(
					categoryWithStatisticsResponseDto -> categoryWithStatisticsResponseDto.getCategory()
						.getCategoryId())
				.contents(List.of(content0, content1))
				.pageSize(1)
				.sortOrder(SortOrder.DESC)
				.build();
			Mockito.doReturn(response)
				.when(categoryservice)
				.findCursorPagingCategoryWithHistoryResponse(anyInt(), any(), any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/categories/me")
				.header("Authorization", "Bearer " + jwtToken)
				.queryParam("pageSize", "4")
				.queryParam("hasNext", Id.generateNextId().toString())
				.queryParam("containTitle", "title"));

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("hasNext").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andDo(document(
					"{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("JWT Bearer 토큰")
					),
					requestParameters(
						parameterWithName("pageSize").description("페이지 사이즈"),
						parameterWithName("hasNext").description("다음 페이지 커서").optional(),
						parameterWithName("containTitle").description("카테고리 제목 포함 여부(null인 경우 전체 검색)").optional()
					),
					docsFieldGeneratorUtils.generateResponseFieldSnippet(
						new ParameterizedTypeReference<CursorPage<CategoryWithStatisticsResponseDto>>() {
						}, "category",
						Locale.KOREAN
					)
				));
		}

		@Test
		@DisplayName("option이 RECOMMEND인 경우 개인 카테고리 커서 페이징 조회 응답 200")
		@WithMockMember
		void shouldReturn200WhenOptionIsRECOMMENDTest() throws Exception {
			//given
			Id memberId = Id.generateNextId();
			Id categoryId = Id.generateNextId();
			Category category = CategoryTestHelper.generateSharedCategory("title", memberId, categoryId);
			Member member = MemberTestHelper.generateMember(memberId);
			CategoryStatisticsDto categoryStatisticsDto = new CategoryStatisticsDto(12.3, 10L, 20L);
			SharedCategoryWithStatisticsAndRecommendDto content = new SharedCategoryWithStatisticsAndRecommendDto(
				category, member, categoryStatisticsDto, 10L);
			CursorPage<SharedCategoryWithStatisticsAndRecommendDto> response = CursorPage.<SharedCategoryWithStatisticsAndRecommendDto>builder()
				.lastIdExtractStrategy(
					sharedCategoryWithStatistics -> sharedCategoryWithStatistics.getCategory().getCategoryId())
				.pageSize(1)
				.contents(List.of(content))
				.sortOrder(SortOrder.DESC)
				.build();
			Mockito.doReturn(response)
				.when(categoryservice)
				.findSharedCategoryWithStatistics(anyInt(), any(), any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/categories/me")
				.header("Authorization", "Bearer " + jwtToken)
				.queryParam("pageSize", "4")
				.queryParam("hasNext", Id.generateNextId().toString())
				.queryParam("containTitle", "title")
				.queryParam("option", "RECOMMEND"));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document(
					"{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("JWT Bearer 토큰")
					),
					requestParameters(
						parameterWithName("pageSize").description("페이지 사이즈"),
						parameterWithName("hasNext").description("다음 페이지 커서").optional(),
						parameterWithName("containTitle").description("카테고리 제목 포함 여부(null인 경우 전체 검색)").optional(),
						parameterWithName("option").description("카테고리 조회 옵션, RECOMMEND: 본인이 추천한 카테고리 조회").optional()
					),
					docsFieldGeneratorUtils.generateResponseFieldSnippet(
						new ParameterizedTypeReference<CursorPage<SharedCategoryWithStatisticsAndRecommendDto>>() {
						}, "category",
						Locale.KOREAN
					)
				));
		}
	}

	@Nested
	@DisplayName("카테고리 삭제")
	class DeleteCategoryTest {

		@Test
		@WithMockMember
		@DisplayName("카테고리 삭제 성공시 응답 200")
		void shouldReturnStatus200WhenSuccessTest() throws Exception {
			// given
			Id categoryId = Id.generateNextId();

			// when
			ResultActions resultActions = mockMvc.perform(delete("/api/v1/categories/{categoryId}", categoryId)
				.header("Authorization", "Bearer " + jwtToken));

			resultActions.andExpect(status().isOk())
				.andDo(document(
					"{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("JWT Bearer 토큰")
					),
					pathParameters(
						parameterWithName("categoryId").description("카테고리 아이디")
					)
				));
		}
	}

	@Nested
	@DisplayName("카테고리 공유 커서 페이징")
	class SearchShareCategoryTest {

		@Test
		@DisplayName("카테고리 공유 커서 페이징 성공시 응답 200 및 정상 응답")
		void shouldReturnStatus200AndResponseWhenSuccessTest() throws Exception {
			// given
			CursorPage<SharedCategoryResponseDto> cursorPage = CursorPage.<SharedCategoryResponseDto>builder()
				.lastIdExtractStrategy(sharedCategoryDto -> sharedCategoryDto.getCategory().getCategoryId())
				.pageSize(1)
				.contents(List.of(new SharedCategoryResponseDto(
					CategoryTestHelper.generateSharedCategory("title", Id.generateNextId(), Id.generateNextId()),
					MemberTestHelper.generateMember(Id.generateNextId()),
					1L), new SharedCategoryResponseDto(
					CategoryTestHelper.generateSharedCategory("title2", Id.generateNextId(), Id.generateNextId()),
					MemberTestHelper.generateMember(Id.generateNextId()),
					1L)))
				.sortOrder(SortOrder.DESC)
				.build();
			Mockito.doReturn(cursorPage)
				.when(categoryservice)
				.findCursorPagingSharedCategoryResponseDto(anyInt(), any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/categories/share")
				.queryParam("hasNext", Id.generateNextId().toString())
				.queryParam("containTitle", "title")
				.queryParam("pageSize", "2"));

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("pageSize").exists())
				.andExpect(jsonPath("hasNext").exists())
				.andExpect(jsonPath("sortOrder").exists())
				.andExpect(jsonPath("contents").exists())
				.andDo(document(
						"{class-name}/{method-name}",
						getDocumentRequest(),
						getDocumentResponse(),
						requestParameters(
							parameterWithName("hasNext").description("다음 페이지 커서").optional(),
							parameterWithName("containTitle").description("카테고리 제목 포함 여부(null인 경우 전체 검색)").optional(),
							parameterWithName("pageSize").description("페이지 사이즈")
						),
						docsFieldGeneratorUtils.generateResponseFieldSnippet(
							new ParameterizedTypeReference<CursorPage<SharedCategoryResponseDto>>() {
							}, "category",
							Locale.KOREAN
						)
					)
				);
		}
	}

	@Nested
	@DisplayName("카테고리 추천 등록 API")
	class RecommendTest {

		@Test
		@WithMockMember
		@DisplayName("카테고리 추천 등롱 성공시 응답 200")
		void shouldReturnStatus200AndResponseWhenSuccessTest() throws Exception {
			// given

			// when
			ResultActions resultActions = mockMvc.perform(
				post("/api/v1/categories/{categoryId}/like/like", Id.generateNextId())
					.header("Authorization", "Bearer " + jwtToken));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("JWT Bearer 토큰")
					),
					pathParameters(
						parameterWithName("categoryId").description("카테고리 아이디")
					)
				));
		}
	}

	@Nested
	@DisplayName("카테고리 추천 취소 API")
	class CancelTest {

		@Test
		@WithMockMember
		@DisplayName("카테고리 추천 취소 성공시 응답 200")
		void shouldReturnStatus200AndResponseWhenSuccessTest() throws Exception {
			// given
			Id categoryId = Id.generateNextId();

			// when
			ResultActions resultActions = mockMvc.perform(
				post("/api/v1/categories/{categoryId}/like/unlike", categoryId)
					.header("Authorization", "Bearer " + jwtToken));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("JWT Bearer 토큰")
					),
					pathParameters(
						parameterWithName("categoryId").description("카테고리 아이디")
					)
				));
		}
	}

	@Nested
	@DisplayName("카테고리 추천 여부 확인 API")
	class IsRecommendCategoriesTest {

		@Test
		@DisplayName("카테고리 추천 여부 확인 성공시 응답 200")
		@WithMockMember
		void shouldReturnStatus200WhenSuccessTest() throws Exception {
			// given
			Id categoryId = Id.generateNextId();
			Mockito.doReturn(true)
				.when(categoryRecommendService)
				.isRecommended(any(), any());

			// when
			ResultActions resultActions = mockMvc.perform(
				get("/api/v1/categories/{categoryId}/like", categoryId)
					.header("Authorization",
						"Bearer " + jwtToken));

			// then
			resultActions.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("JWT Bearer 토큰")
					),
					pathParameters(
						parameterWithName("categoryId").description("카테고리 아이디")
					),
					responseFields(
						fieldWithPath("liked").description("카테고리 추천 여부")
					)
				));
		}
	}
}