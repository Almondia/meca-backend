package com.almondia.meca.auth.oauth.service;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.almondia.meca.auth.oauth.infra.CustomOAuth2Client;
import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.auth.oauth.infra.dto.OAuth2AccessTokenResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class Oauth2Service {

	private final CustomOAuth2Client oAuth2Client;

	public OAuth2UserAttribute requestUserInfo(String registrationId, String authorizationCode) {
		Mono<OAuth2AccessTokenResponse> response = oAuth2Client.requestAccessToken(registrationId, authorizationCode);
		OAuth2AccessTokenResponse tokenResponse = response.block();
		Mono<Map<String, Object>> monoUserInfo = oAuth2Client.requestUserInfo(registrationId,
			Objects.requireNonNull(tokenResponse));
		UserInfoExtractor extractor = UserInfoExtractor.getInstance(registrationId);
		return extractor.extract(monoUserInfo.block());
	}
}
