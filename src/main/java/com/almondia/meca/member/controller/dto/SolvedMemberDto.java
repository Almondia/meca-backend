package com.almondia.meca.member.controller.dto;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.vo.Name;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class SolvedMemberDto {

	private final Id solvedMemberId;
	private final Name solvedMemberName;
}
