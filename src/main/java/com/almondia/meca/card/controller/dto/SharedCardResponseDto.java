package com.almondia.meca.card.controller.dto;

import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.member.application.helper.MemberMapper;
import com.almondia.meca.member.controller.dto.MemberDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;

@Getter
public class SharedCardResponseDto {

	private final CardDto cardInfo;
	private final MemberDto memberInfo;

	public SharedCardResponseDto(Card card, Member member) {
		this.cardInfo = CardMapper.cardToDto(card);
		this.memberInfo = MemberMapper.fromEntityToDto(member);
	}
}
