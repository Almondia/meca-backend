package com.almondia.meca.member.service;

import org.springframework.stereotype.Service;

import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public Member save(OAuth2UserAttribute oauth2UserAttribute) {
		Member member = Member.builder()
			.memberId(Id.generateNextId())
			.name(new Name(oauth2UserAttribute.getName()))
			.oAuthType(oauth2UserAttribute.getOauthType())
			.email(new Email(oauth2UserAttribute.getEmail()))
			.role(Role.USER)
			.build();
		memberRepository.save(member);
		return member;
	}

	public Member findMember(Id memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));
	}
}
