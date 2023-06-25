package com.almondia.meca.cardhistory.infra.morpheme;

import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.almondia.meca.cardhistory.domain.service.MorphemeAnalyzer;
import com.almondia.meca.cardhistory.infra.morpheme.token.EngNlpToken;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component("englishMorphemeAnalyzer")
@Primary
@RequiredArgsConstructor
public class EnglishMorphemeAnalyzer implements MorphemeAnalyzer<EngNlpToken> {

	private final WebClient webClient;
	private final Environment environment;

	@Override
	public Morphemes<EngNlpToken> analyze(String cardAnswer, String userAnswer) {
		String requestUri = environment.getProperty("morpheme_uri.english");
		if (requestUri == null) {
			throw new IllegalArgumentException("morpheme_uri.english is null");
		}
		return webClient.put()
			.uri(requestUri)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(new CommonRequest(cardAnswer, userAnswer))
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Morphemes<EngNlpToken>>() {
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
