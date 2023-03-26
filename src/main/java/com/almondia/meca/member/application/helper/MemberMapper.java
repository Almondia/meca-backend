package com.almondia.meca.member.application.helper;

import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.domain.entity.Member;

public class MemberMapper {

	public static MemberResponseDto fromEntityToDto(Member member) {
		return MemberResponseDto.builder()
			.memberId(member.getMemberId())
			.name(member.getName())
			.email(member.getEmail())
			.oAuthType(member.getOAuthType())
			.role(member.getRole())
			.isDeleted(member.isDeleted())
			.createdAt(member.getCreatedAt())
			.modifiedAt(member.getModifiedAt())
			.build();
	}
}
