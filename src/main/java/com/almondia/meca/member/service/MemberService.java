package com.almondia.meca.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.member.repository.MemberRepository;
import com.almondia.meca.member.service.helper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public Member save(OAuth2UserAttribute oauth2UserAttribute) {
		Email email = oauth2UserAttribute.getEmail() == null ? null : new Email(oauth2UserAttribute.getEmail());
		Member member = Member.builder()
			.memberId(Id.generateNextId())
			.oauthId(oauth2UserAttribute.getOAuthId())
			.name(new Name(oauth2UserAttribute.getName()))
			.oAuthType(oauth2UserAttribute.getOauthType())
			.email(email)
			.role(Role.USER)
			.build();
		memberRepository.save(member);
		return member;
	}

	@Transactional(readOnly = true)
	public Member findMember(Id memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));
	}

	@Transactional(readOnly = true)
	public MemberResponseDto findMyProfile(Id memberId) {
		Member member = findMember(memberId);
		return MemberMapper.fromEntityToDto(member);
	}
}
