package com.almondia.meca.member.controller;

import static com.almondia.meca.asciidocs.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.application.MemberService;
import com.almondia.meca.member.application.helper.MemberMapper;
import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.controller.dto.UpdateMemberRequestDto;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MemberController.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({JacksonConfiguration.class})
class MemberControllerTest {

	private static final String jwtToken = "jwt token";

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.alwaysDo(print())
			.apply(documentationConfiguration(restDocumentation))
			.build();
	}

	/**
	 * 응답 성공시 리턴값 검증
	 */
	@Nested
	@DisplayName("개인 프로필 조회")
	class FindMyProfile {

		@Test
		@DisplayName("응답 성공시 리턴값 검증")
		@WithMockMember
		void shouldReturnMemberResponseDtoAttributesWhenReturn200Test() throws Exception {
			//given
			Mockito.doReturn(MemberTestHelper.genMemberResponseDto())
				.when(memberService)
				.findMyProfile(any());

			// when
			ResultActions result = mockMvc.perform(get("/api/v1/members/me")
				.header("Authorization", "Bearer " + jwtToken));

			// then
			result.andExpect(status().isOk())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("email").exists())
				.andExpect(jsonPath("profile").exists())
				.andExpect(jsonPath("oauthType").exists())
				.andExpect(jsonPath("role").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("Bearer Token")
					),
					responseFields(
						fieldWithPath("memberId").description("회원 아이디"),
						fieldWithPath("name").description("회원 이름"),
						fieldWithPath("email").description("회원 이메일"),
						fieldWithPath("profile").description("회원 프로필 사진"),
						fieldWithPath("oauthType").description("회원 소셜 로그인 타입"),
						fieldWithPath("role").description("회원 권한"),
						fieldWithPath("deleted").description("회원 삭제 여부"),
						fieldWithPath("createdAt").description("회원 생성일"),
						fieldWithPath("modifiedAt").description("회원 수정일")
					)
				));
		}
	}

	@Nested
	@DisplayName("개인 프로필 수정")
	class UpdateMyProfileTest {

		@Test
		@DisplayName("응답 성공시 리턴값 검증")
		@WithMockMember
		void shouldReturnMemberResponseDtoAttributesWhenReturn200Test() throws Exception {
			//given
			UpdateMemberRequestDto updateMemberRequestDto = UpdateMemberRequestDto.of(Name.of("name"),
				new Image("https://aws.s3.com/1234"));
			Member member = MemberTestHelper.generateMember(Id.generateNextId());
			member.updateName(updateMemberRequestDto.getName());
			member.updateProfile(updateMemberRequestDto.getProfile());
			MemberResponseDto memberResponseDto = MemberMapper.fromEntityToDto(member);
			Mockito.doReturn(memberResponseDto)
				.when(memberService)
				.update(any(), any());

			// when
			ResultActions result = mockMvc.perform(put("/api/v1/members/me")
				.header("Authorization", "Bearer " + jwtToken)
				.content(objectMapper.writeValueAsString(updateMemberRequestDto))
				.contentType("application/json"));

			// then
			result.andExpect(status().isOk())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("email").exists())
				.andExpect(jsonPath("profile").exists())
				.andExpect(jsonPath("oauthType").exists())
				.andExpect(jsonPath("role").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists())
				.andDo(document("{class-name}/{method-name}",
					getDocumentRequest(),
					getDocumentResponse(),
					requestHeaders(
						headerWithName("Authorization").description("Bearer Token")
					),
					requestFields(
						fieldWithPath("name").description("수정할 회원 이름").optional().attributes(
							Attributes.key("constraints").value("이름은 빈 공백 없이 2자 이상 10자 이하로 입력해주세요.")),
						fieldWithPath("profile").description("수정할 회원 프로필 사진").optional().attributes(
							Attributes.key("constraints").value("프로필 사진 URI는 255 이하로 입력해주세요.")
						)
					),
					responseFields(
						fieldWithPath("memberId").description("회원 아이디"),
						fieldWithPath("name").description("회원 이름"),
						fieldWithPath("email").description("회원 이메일"),
						fieldWithPath("profile").description("회원 프로필 사진"),
						fieldWithPath("oauthType").description("회원 소셜 로그인 타입"),
						fieldWithPath("role").description("회원 권한"),
						fieldWithPath("deleted").description("회원 삭제 여부"),
						fieldWithPath("createdAt").description("회원 생성일"),
						fieldWithPath("modifiedAt").description("회원 수정일")
					)
				));
		}

	}

}