package com.almondia.meca.helper;

import java.time.LocalDateTime;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.member.controller.dto.MemberDto;
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
			.name(Name.of("name"))
			.profile(new Image("https://aws.s3.com"))
			.oauthId(Id.generateNextId().toString())
			.oAuthType(OAuthType.GOOGLE)
			.role(Role.USER)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static MemberDto genMemberResponseDto() {
		return MemberDto.builder()
			.memberId(Id.generateNextId())
			.name(Name.of("name"))
			.email(new Email("email@naver.com"))
			.profile(new Image("profile"))
			.oauthType(OAuthType.KAKAO)
			.role(Role.USER)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static MemberDto generateMemberResponseDto(Id memberId) {
		return MemberDto.builder()
			.memberId(memberId)
			.name(Name.of("name"))
			.email(new Email("email@naver.com"))
			.profile(new Image("profile"))
			.oauthType(OAuthType.KAKAO)
			.role(Role.USER)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}
}
