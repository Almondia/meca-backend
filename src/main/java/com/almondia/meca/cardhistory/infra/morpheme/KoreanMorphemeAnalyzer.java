package com.almondia.meca.cardhistory.infra.morpheme;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.almondia.meca.cardhistory.domain.service.MorphemeAnalyzer;
import com.almondia.meca.cardhistory.infra.morpheme.token.KoNlpToken;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@Qualifier("koreanMorphemeAnalyzer")
@RequiredArgsConstructor
public class KoreanMorphemeAnalyzer implements MorphemeAnalyzer<KoNlpToken> {

	private final WebClient webClient;
	private final Environment environment;

	@Override
	public Morphemes<KoNlpToken> analyze(String cardAnswer, String userAnswer) {
		String requestUri = environment.getProperty("morpheme_uri.korean");
		if (requestUri == null) {
			throw new IllegalArgumentException("morpheme_uri.korean is null");
		}
		return webClient.put()
			.uri(requestUri)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(new CommonRequest(cardAnswer, userAnswer))
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Morphemes<KoNlpToken>>() {
			})
			.block();
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	private static class CommonRequest {

		private String cardAnswer;
		private String userAnswer;
	}
}
