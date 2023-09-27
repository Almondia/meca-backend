package com.almondia.meca.auth.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.auth.jwt.service.JwtTokenService;
import com.almondia.meca.auth.oauth.controller.dto.AccessTokenResponseDto;
import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.auth.oauth.service.Oauth2Service;
import com.almondia.meca.member.application.MemberService;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/oauth")
@Slf4j
public class OauthController {

	private final Oauth2Service oauth2Service;
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final JwtTokenService jwtTokenService;

	@PostMapping("/login/{registrationId}")
	public ResponseEntity<AccessTokenResponseDto> loginUser(
		@PathVariable("registrationId") String registrationId,
		@RequestParam("code") String authorizationCode) {
		HttpStatus statusResponse = HttpStatus.OK;
		OAuth2UserAttribute oauth2UserAttribute = oauth2Service.requestUserInfo(registrationId,
			authorizationCode);
		Member member = memberRepository.findByOauthId(oauth2UserAttribute.getOAuthId())
			.orElse(null);
		if (member == null) {
			member = memberService.save(oauth2UserAttribute);
			statusResponse = HttpStatus.CREATED;
		}
		String accessToken = jwtTokenService.createToken(member.getMemberId());
		return ResponseEntity.status(statusResponse).body(AccessTokenResponseDto.of(accessToken));
	}
}
