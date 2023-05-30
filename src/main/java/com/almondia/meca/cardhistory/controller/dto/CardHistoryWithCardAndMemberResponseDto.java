package com.almondia.meca.cardhistory.controller.dto;

import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.controller.dto.SolvedMemberDto;
import com.almondia.meca.member.domain.vo.Name;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class CardHistoryWithCardAndMemberResponseDto {

	private final CardHistoryDto cardHistory;
	private final SolvedMemberDto solvedMember;
	private final CardDto card;

	public CardHistoryWithCardAndMemberResponseDto(CardHistory cardHistory, Card card, Id memberId,
		Name solverMemberName) {
		this.cardHistory = new CardHistoryDto(cardHistory);
		this.card = CardMapper.cardToDto(card);
		this.solvedMember = new SolvedMemberDto(memberId, solverMemberName);
	}
}
