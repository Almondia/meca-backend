package com.almondia.meca.common.configuration.security;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.almondia.meca.member.application.MemberService;
import com.almondia.meca.mock.security.WithMockMember;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTest {

	@Autowired
	WebApplicationContext webCtx;

	@Autowired
	MockMvc mockMvc;

	@MockBean
	MemberService memberService;

	@BeforeEach
	void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webCtx)
			.apply(springSecurity())
			.alwaysDo(print())
			.build();
	}

	@Test
	@WithMockMember
	void test() throws Exception {
		mockMvc.perform(get("/api/v1/members/me")
				.header(HttpHeaders.ORIGIN, "http://localhost:3001"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockMember
	void test2() throws Exception {
		mockMvc.perform(get("/api/v1/members/me")
				.header(HttpHeaders.ORIGIN, "http://localhost:3000"))
			.andExpect(status().isOk());
	}
}