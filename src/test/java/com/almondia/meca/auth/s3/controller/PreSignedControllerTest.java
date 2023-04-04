package com.almondia.meca.auth.s3.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.configuration.web.WebMvcConfiguration;
import com.almondia.meca.common.infra.s3.S3PreSignedUrlRequest;
import com.almondia.meca.mock.security.WithMockMember;

@WebMvcTest(PreSignedController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({JacksonConfiguration.class, WebMvcConfiguration.class})
class PreSignedControllerTest {

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	S3PreSignedUrlRequest s3PreSignedUrlRequest;

	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("응답 성공시 Upload PresignedUrl를 리턴해야함")
	@WithMockMember
	void successResponseOkAndGetUrlTest() throws Exception {
		URL url = UriComponentsBuilder.fromUriString("https://www.abc.com").build().toUri().toURL();
		Mockito.doReturn(url).when(s3PreSignedUrlRequest).requestPutPreSignedUrl(any(), any());
		mockMvc.perform(get("/api/v1/presign/images/upload?purpose=thumbnail&extension=jpg"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("url").exists())
			.andExpect(jsonPath("expirationDate").exists())
			.andExpect(jsonPath("objectKey").exists());
	}

	@Test
	@DisplayName("응답 성공시 Download PresignedUrl를 리턴해야함")
	@WithMockMember
	void successResponseOkAndGetDownloadUrlTest() throws Exception {
		URL url = UriComponentsBuilder.fromUriString("https://www.abc.com").build().toUri().toURL();
		Mockito.doReturn(url).when(s3PreSignedUrlRequest).requestGetPreSignedUrl(any(), any());
		mockMvc.perform(get("/api/v1/presign/images/download?objectKey=abc"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("url").exists())
			.andExpect(jsonPath("expirationDate").exists());
	}
}