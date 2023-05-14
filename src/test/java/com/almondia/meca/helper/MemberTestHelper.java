package com.almondia.meca.helper;

import java.time.LocalDateTime;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;

public class MemberTestHelper {

	public static Member generateMember(Id memberId) {
		return Member.builder()
			.memberId(memberId)
			.email(new Email("www@gmail.com"))
			.name(new Name("name"))
			.profile(new Image("https://aws.s3.com"))
			.oauthId(Id.generateNextId().toString())
			.oAuthType(OAuthType.GOOGLE)
			.role(Role.USER)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static MemberResponseDto genMemberResponseDto() {
		return MemberResponseDto.builder()
			.memberId(Id.generateNextId())
			.name(new Name("name"))
			.email(new Email("email@naver.com"))
			.profile(new Image("profile"))
			.oAuthType(OAuthType.KAKAO)
			.role(Role.USER)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static MemberResponseDto generateMemberResponseDto(Id memberId) {
		return MemberResponseDto.builder()
			.memberId(memberId)
			.name(new Name("name"))
			.email(new Email("email@naver.com"))
			.profile(new Image("profile"))
			.oAuthType(OAuthType.KAKAO)
			.role(Role.USER)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}
}
