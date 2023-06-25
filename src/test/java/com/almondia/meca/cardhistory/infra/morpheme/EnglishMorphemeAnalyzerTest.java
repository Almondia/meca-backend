package com.almondia.meca.cardhistory.infra.morpheme;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.almondia.meca.cardhistory.domain.service.MorphemeAnalyzer;
import com.almondia.meca.cardhistory.infra.morpheme.token.NlpToken;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
class EnglishMorphemeAnalyzerTest {

	private static MockWebServer mockWebServer;

	@Autowired
	private WebClient webClient;
	private MorphemeAnalyzer<? extends NlpToken> englishMorphemeAnalyzer;

	@BeforeAll
	static void before() throws IOException {
		mockWebServer = makeMockWebServer();
	}

	@AfterAll
	static void after() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void throwArgumentExceptionWhenCannotCallRequestUri() {
		// given
		Environment environment = Mockito.mock(Environment.class);
		WebClient mockWebClient = Mockito.mock(WebClient.class);
		englishMorphemeAnalyzer = new EnglishMorphemeAnalyzer(mockWebClient, environment);

		// expect
		assertThatThrownBy(() ->
			englishMorphemeAnalyzer.analyze("안녕 디지몬", "디지몬 안녕"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void test2() {
		// given
		String REDIRECT_URI = String.format("http://localhost:%s", mockWebServer.getPort());
		Environment environment = Mockito.mock(Environment.class);
		Mockito.when(environment.getProperty("morpheme_uri.english")).thenReturn(REDIRECT_URI);
		englishMorphemeAnalyzer = new EnglishMorphemeAnalyzer(webClient, environment);

		// when
		Morphemes<? extends NlpToken> result = englishMorphemeAnalyzer.analyze("스키마란 데이터베이스의 구조와 제약조건을 설정과 관련된 것입니다",
			"스키마란 데이터베이스의 구조와 제약조건에 관해 전반적인 명세를 한 것을 의미합니다");

		// then
		assertThat(result).hasFieldOrProperty("cardAnswerMorpheme")
			.hasFieldOrProperty("userAnswerMorpheme");

	}

	private static MockWebServer makeMockWebServer() throws IOException {
		MockWebServer mockWebServer = new MockWebServer();
		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(HttpStatus.OK.value())
			.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.setBody(
				"{\"cardAnswerMorpheme\":[{\"morph\":\"SCHEMA\",\"pos\":\"NNP\"},{\"morph\":\"OVERALL\",\"pos\":\"NNP\"},{\"morph\":\"SPECIFICATION\",\"pos\":\"NNP\"},{\"morph\":\"STRUCTURE\",\"pos\":\"NNP\"},{\"morph\":\"CONSTRAINTS\",\"pos\":\"NNP\"},{\"morph\":\"DATABASE\",\"pos\":\"NNP\"}],\"userAnswerMorpheme\":[{\"morph\":\"SCHEMA\",\"pos\":\"NNP\"},{\"morph\":\"CONCERNED\",\"pos\":\"NNP\"},{\"morph\":\"SETTING\",\"pos\":\"NNP\"},{\"morph\":\"STRUCTURE\",\"pos\":\"NNP\"},{\"morph\":\"CONSTRAINTS\",\"pos\":\"NNP\"},{\"morph\":\"DATABASE\",\"pos\":\"NNP\"}]}")
		);
		mockWebServer.start();
		return mockWebServer;
	}
}