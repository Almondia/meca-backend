package com.almondia.meca.card.controller.dto;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.member.domain.vo.Name;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardMemberDto {
	private final Id memberId;
	private final Name name;
	private final Image profile;
}
