package com.almondia.meca.member.controller.dto;

import java.time.LocalDateTime;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class MemberResponseDto {

	private final Id memberId;
	private final Name name;
	private final Email email;
	private final Image profile;
	private final OAuthType oAuthType;
	private final Role role;
	private final boolean isDeleted;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
}
