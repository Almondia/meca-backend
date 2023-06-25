package com.almondia.meca.common.configuration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class AppConfiguration {

	private final ObjectMapper objectMapper;

	@Bean
	public WebClient getWebClient() {
		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(getHttpClient()))
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.codecs(configurer -> {
				configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
				configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
			})
			.filter(new WebClientExceptionHandler())
			.build();
	}

	private HttpClient getHttpClient() {
		return HttpClient.create()
			.responseTimeout(Duration.ofSeconds(5))
			.doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(5))
				.addHandlerLast(new WriteTimeoutHandler(5)));
	}

}