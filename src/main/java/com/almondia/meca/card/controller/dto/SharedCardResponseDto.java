package com.almondia.meca.card.controller.dto;

import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.member.application.helper.MemberMapper;
import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;

@Getter
public class SharedCardResponseDto {

	private final CardDto cardInfo;
	private final MemberResponseDto memberInfo;

	public SharedCardResponseDto(Card card, Member member) {
		this.cardInfo = CardMapper.cardToDto(card);
		this.memberInfo = MemberMapper.fromEntityToDto(member);
	}
}
