package com.almondia.meca.common.configuration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class AppConfiguration {

	@Bean
	public WebClient getWebClient() {
		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(getHttpClient()))
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();
	}

	private HttpClient getHttpClient() {
		return HttpClient.create()
			.responseTimeout(Duration.ofSeconds(5))
			.doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(5))
				.addHandlerLast(new WriteTimeoutHandler(5)));
	}

}