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
			String oAuthId = response.getString("id");
			String name = response.getString("name");
			String email = null;
			if (response.has("email")) {
				email = response.getString("email");
			}
			return OAuth2UserAttribute.of(oAuthId, name, email, OAuthType.NAVER);
		}
	},
	KAKAO("kakao") {
		@Override
		public OAuth2UserAttribute extract(Map<String, Object> userInfoJson) {
			JSONObject userInfo = new JSONObject(userInfoJson);
			JSONObject properties = userInfo.getJSONObject("properties");
			JSONObject kakaoAccount = userInfo.getJSONObject("kakao_account");
			String oAuthId = String.valueOf(userInfoJson.get("id"));
			String name = properties.getString("nickname");
			String email = null;
			if (kakaoAccount.has("email")) {
				email = kakaoAccount.getString("email");
			}
			return OAuth2UserAttribute.of(oAuthId, name, email, OAuthType.KAKAO);
		}
	},
	GOOGLE("google") {
		@Override
		public OAuth2UserAttribute extract(Map<String, Object> userInfoJson) {
			JSONObject userInfo = new JSONObject(userInfoJson);
			String oAuthId = userInfo.getString("sub");
			String email = null;
			if (userInfo.has("email")) {
				email = userInfo.getString("email");
			}
			String name = userInfo.getString("name");
			return OAuth2UserAttribute.of(oAuthId, name, email, OAuthType.GOOGLE);
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
