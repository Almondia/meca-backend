package com.almondia.meca.auth.oauth.infra;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class FakeClientRegistrationRepository implements ClientRegistrationRepository {

	private final Map<String, ClientRegistration> map = new HashMap<>();

	public void addRegistration(ClientRegistration clientRegistration) {
		map.put(clientRegistration.getRegistrationId(), clientRegistration);
	}

	@Override
	public ClientRegistration findByRegistrationId(String registrationId) {
		return map.get(registrationId);
	}
}
