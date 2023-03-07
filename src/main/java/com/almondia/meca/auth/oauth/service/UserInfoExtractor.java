package com.almondia.meca.auth.oauth.service;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONObject;

import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.member.domain.vo.OAuthType;

public enum UserInfoExtractor {
	NAVER("naver") {
		@Override
		public OAuth2UserAttribute extract(Map<String, Object> userInfoJson) {
			JSONObject userInfo = new JSONObject(userInfoJson);
			JSONObject response = userInfo.getJSONObject("response");
			String name = response.getString("name");
			String email = response.getString("email");
			return OAuth2UserAttribute.of(name, email, OAuthType.NAVER);
		}
	},
	KAKAO("kakao") {
		@Override
		public OAuth2UserAttribute extract(Map<String, Object> userInfoJson) {
			JSONObject userInfo = new JSONObject(userInfoJson);
			JSONObject properties = userInfo.getJSONObject("properties");
			JSONObject kakaoAccount = userInfo.getJSONObject("kakao_account");
			String name = properties.getString("nickname");
			String email = kakaoAccount.getString("email");
			return OAuth2UserAttribute.of(name, email, OAuthType.KAKAO);
		}
	},
	GOOGLE("google") {
		@Override
		public OAuth2UserAttribute extract(Map<String, Object> userInfoJson) {
			JSONObject userInfo = new JSONObject(userInfoJson);
			String email = userInfo.getString("email");
			String name = userInfo.getString("name");
			return OAuth2UserAttribute.of(name, email, OAuthType.GOOGLE);
		}
	};

	private static final String NOT_FOUND_INSTANCE_MESSAGE = "not found client name";

	private final String clientName;

	UserInfoExtractor(String clientName) {
		this.clientName = clientName;
	}

	public static UserInfoExtractor getInstance(String clientName) {
		return Arrays.stream(UserInfoExtractor.values())
			.filter(userInfoExtractor -> clientName.equals(userInfoExtractor.clientName))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_INSTANCE_MESSAGE));
	}

	public abstract OAuth2UserAttribute extract(Map<String, Object> userInfoJson);
}
