package com.almondia.meca.card.controller.dto;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.member.domain.entity.Member;

import lombok.Getter;

@Getter
public class CardResponseDto {
	private final CardDto card;
	private final CardMemberDto member;

	public CardResponseDto(CardDto card, Member member) {
		this.card = card;
		this.member = toMemberDto(member);
	}

	public CardResponseDto(Card card, Member member) {
		this.card = toCardDto(card);
		this.member = toMemberDto(member);
	}

	private CardDto toCardDto(Card card) {
		return CardDto.builder()
			.cardId(card.getCardId())
			.title(card.getTitle())
			.memberId(card.getMemberId())
			.question(card.getQuestion())
			.categoryId(card.getCategoryId())
			.cardType(card.getCardType())
			.createdAt(card.getCreatedAt())
			.modifiedAt(card.getModifiedAt())
			.answer(card.getAnswer())
			.description(card.getDescription())
			.build();
	}

	private CardMemberDto toMemberDto(Member member) {
		return CardMemberDto.builder()
			.memberId(member.getMemberId())
			.name(member.getName())
			.profile(member.getProfile())
			.build();
	}
}
