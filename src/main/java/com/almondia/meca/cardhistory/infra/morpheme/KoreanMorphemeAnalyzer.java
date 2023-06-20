package com.almondia.meca.cardhistory.infra.morpheme;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.almondia.meca.cardhistory.domain.service.MorphemeAnalyzer;
import com.almondia.meca.cardhistory.domain.vo.NlpToken;

import lombok.RequiredArgsConstructor;

@Component("koreanMorphemeAnalyzer")
@RequiredArgsConstructor
public class KoreanMorphemeAnalyzer implements MorphemeAnalyzer {

	private final WebClient webClient;

	@Override
	public List<NlpToken> analyze(String text) {
		return webClient.get()
			.uri("http://localhost:8070/api/v1/konlp/analyze?text=" + text)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<List<NlpToken>>() {
			})
			.block();
	}
}
