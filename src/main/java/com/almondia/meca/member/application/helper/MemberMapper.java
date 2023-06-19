package com.almondia.meca.member.application.helper;

import com.almondia.meca.member.controller.dto.MemberDto;
import com.almondia.meca.member.domain.entity.Member;

public class MemberMapper {

	public static MemberDto fromEntityToDto(Member member) {
		return MemberDto.builder()
			.memberId(member.getMemberId())
			.name(member.getName())
			.email(member.getEmail())
			.profile(member.getProfile())
			.oauthType(member.getOAuthType())
			.role(member.getRole())
			.isDeleted(member.isDeleted())
			.createdAt(member.getCreatedAt())
			.modifiedAt(member.getModifiedAt())
			.build();
	}
}
