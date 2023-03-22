package com.almondia.meca.auth.oauth.infra.attribute;

import com.almondia.meca.member.domain.vo.OAuthType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class OAuth2UserAttribute {

	private final String oAuthId;
	private final String name;
	private final String email;
	private final OAuthType oauthType;

	public static OAuth2UserAttribute of(String oAuthId, String name, String email, OAuthType oauthType) {
		return new OAuth2UserAttribute(oAuthId, name, email, oauthType);
	}
}
