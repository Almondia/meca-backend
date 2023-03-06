package com.almondia.meca.auth.oauth.infra;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.almondia.meca.auth.oauth.infra.dto.OAuth2AccessTokenResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomOAuth2Client {

	private final ClientRegistrationRepository clientRegistrationRepository;
	private final WebClient webClient;

	public Mono<OAuth2AccessTokenResponse> requestAccessToken(String registrationId,
		String authorizationCode) {
		ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(
			registrationId);
		return webClient.post()
			.uri(requestAccessTokenUri(registration, authorizationCode))
			.retrieve()
			.bodyToMono(OAuth2AccessTokenResponse.class);
	}

	private URI requestAccessTokenUri(ClientRegistration registration,
		String authorizationCode) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", registration.getClientId());
		params.add("redirect_uri", registration.getRedirectUri());
		params.add("code", authorizationCode);
		params.add("client_secret", registration.getClientSecret());

		return UriComponentsBuilder
			.fromUriString(registration.getProviderDetails().getTokenUri())
			.queryParams(params)
			.build()
			.encode(StandardCharsets.UTF_8)
			.toUri();
	}
}