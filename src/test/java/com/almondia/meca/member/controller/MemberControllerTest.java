package com.almondia.meca.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.test.web.servlet.MockMvc;

import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.application.MemberService;
import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.mock.security.WithMockMember;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({JacksonConfiguration.class})
class MemberControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	MemberService memberService;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	ObjectMapper objectMapper;

	/**
	 * 1. 개인 프로필 조회 응답 성공시 리턴값 검증
	 */
	@Nested
	@DisplayName("개인 프로필 조회")
	class getMyProfile {

		@Test
		@DisplayName("개인 프로필 조회 응답 성공시 리턴값 검증")
		@WithMockMember
		void shouldReturnMemberResponseDtoAttributesWhenReturn200Test() throws Exception {
			Mockito.doReturn(MemberResponseDto.builder()
				.memberId(Id.generateNextId())
				.name(new Name("name"))
				.email(new Email("email@naver.com"))
				.oAuthType(OAuthType.KAKAO)
				.role(Role.USER)
				.isDeleted(false)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.build()).when(memberService).findMyProfile(any());

			mockMvc.perform(get("/api/v1/members/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("memberId").exists())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("email").exists())
				.andExpect(jsonPath("oauthType").exists())
				.andExpect(jsonPath("role").exists())
				.andExpect(jsonPath("deleted").exists())
				.andExpect(jsonPath("createdAt").exists())
				.andExpect(jsonPath("modifiedAt").exists());
		}
	}

}