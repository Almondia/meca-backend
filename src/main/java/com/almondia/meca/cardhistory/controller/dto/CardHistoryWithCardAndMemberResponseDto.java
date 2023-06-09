package com.almondia.meca.cardhistory.controller.dto;

import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
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
	private final CardSnapShotDto card;

	public CardHistoryWithCardAndMemberResponseDto(CardHistory cardHistory, Id cardId, Id memberId,
		Name solverMemberName,
		CardSnapShot cardSnapShot) {
		this.cardHistory = new CardHistoryDto(cardHistory);
		this.card = new CardSnapShotDto(cardId, cardSnapShot);
		this.solvedMember = new SolvedMemberDto(memberId, solverMemberName);
	}
}
