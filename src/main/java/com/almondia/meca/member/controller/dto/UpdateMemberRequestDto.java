package com.almondia.meca.member.controller.dto;

import org.springframework.lang.Nullable;

import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.member.domain.vo.Name;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UpdateMemberRequestDto {

	@Nullable
	private Name name;

	@Nullable
	private Image profile;

	public UpdateMemberRequestDto(@Nullable Name name, @Nullable Image profile) {
		this.name = name;
		this.profile = profile;
	}

	public static UpdateMemberRequestDto of(Name name, Image profile) {
		return new UpdateMemberRequestDto(name, profile);
	}
}
