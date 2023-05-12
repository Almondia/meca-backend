package com.almondia.meca.auth.s3.controller;

import static com.almondia.meca.asciidocs.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.almondia.meca.common.configuration.security.filter.JwtAuthenticationFilter;
import com.almondia.meca.common.configuration.web.WebMvcConfiguration;
import com.almondia.meca.common.infra.s3.S3PreSignedUrlRequest;
import com.almondia.meca.mock.security.WithMockMember;

@WebMvcTest(PreSignedController.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({JacksonConfiguration.class, WebMvcConfiguration.class})
class PreSignedControllerTest {

	private static final String jwtToken = "jwt token";

	@Autowired
	WebApplicationContext context;

	@MockBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	S3PreSignedUrlRequest s3PreSignedUrlRequest;

	MockMvc mockMvc;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.alwaysDo(print())
			.apply(documentationConfiguration(restDocumentation))
			.build();
	}

	@Test
	@DisplayName("응답 성공시 Upload PresignedUrl를 리턴해야함")
	@WithMockMember
	void successResponseOkAndGetUrlTest() throws Exception {
		// given
		URL url = UriComponentsBuilder.fromUriString("https://www.abc.com").build().toUri().toURL();
		Mockito.doReturn(url).when(s3PreSignedUrlRequest).requestPutPreSignedUrl(any(), any());

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/presign/images/upload")
			.header("Authorization", "Bearer " + jwtToken)
			.queryParam("purpose", "thumbnail")
			.queryParam("extension", "jpg")
			.queryParam("fileName", "abc"));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("url").exists())
			.andExpect(jsonPath("expirationDate").exists())
			.andExpect(jsonPath("objectKey").exists())
			.andDo(document(
				"presign/upload",
				getDocumentRequest(),
				getDocumentResponse(),
				requestHeaders(
					headerWithName("Authorization").description("JWT Token")
				),
				requestParameters(
					parameterWithName("purpose").description("이미지 용도(thumbnail,profile,card"),
					parameterWithName("extension").description("파일의 확장자(png,jpeg,jpg,gif"),
					parameterWithName("fileName").description("파일의 이름")
				),
				responseFields(
					fieldWithPath("url").description("S3에 업로드 요청 가능한 presigned url"),
					fieldWithPath("expirationDate").description("URL의 만료 시간"),
					fieldWithPath("objectKey").description("S3에 업로드할 파일의 이름")
				)
			));
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

	@Test
	@DisplayName("응답 성공시 Multi Download PresignedUrl를 리턴해야함")
	@WithMockMember
	void successResponseOkAndGetMultiDownloadUrlTest() throws Exception {
		URL url = UriComponentsBuilder.fromUriString("https://www.abc.com").build().toUri().toURL();
		Mockito.doReturn(url).when(s3PreSignedUrlRequest).requestGetPreSignedUrl(any(), any());
		mockMvc.perform(get("/api/v1/presign/images/download/all?objectKey=abc&objectKey=def"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("urls").exists())
			.andExpect(jsonPath("expirationDate").exists());
	}
}