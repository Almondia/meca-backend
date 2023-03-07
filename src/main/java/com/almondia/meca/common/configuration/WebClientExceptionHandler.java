package com.almondia.meca.common.configuration;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import com.almondia.meca.auth.oauth.exception.BadWebClientRequestException;
import com.almondia.meca.auth.oauth.exception.BadWebClientResponseException;

import reactor.core.publisher.Mono;

public class WebClientExceptionHandler implements ExchangeFilterFunction {

	private static final String BAD_WEB_REQUEST_FORMAT = "4xx 외부 API 요청 오류, status code : %d, response: %s, header: %s";
	private static final String BAD_WEB_RESPONSE_FORMAT = "5xx 외부 API 요청 오류, status code : %d, response: %s, header: %s";

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		return next.exchange(request)
			.flatMap(response -> {
				if (response.statusCode().is4xxClientError()) {
					return Mono.error(new BadWebClientRequestException(
						String.format(BAD_WEB_REQUEST_FORMAT,
							response.rawStatusCode(),
							response.bodyToMono(String.class),
							response.headers().asHttpHeaders()
						)));
				}
				if (response.statusCode().is5xxServerError()) {
					return Mono.error(new BadWebClientResponseException(String.format(BAD_WEB_RESPONSE_FORMAT,
						response.rawStatusCode(),
						response.bodyToMono(String.class),
						response.headers().asHttpHeaders()
					)));
				}
				return Mono.just(response);
			});
	}
}
