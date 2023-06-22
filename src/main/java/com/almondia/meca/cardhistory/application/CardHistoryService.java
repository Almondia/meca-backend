package com.almondia.meca.cardhistory.application;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.application.helper.CardHistoryFactory;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.cardhistory.domain.service.DefaultScoringMachine;
import com.almondia.meca.cardhistory.domain.service.MorphemeAnalyzer;
import com.almondia.meca.cardhistory.domain.service.ScoringMachine;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.cardhistory.infra.morpheme.token.NlpToken;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardHistoryService {

	private static final ScoringMachine<NlpToken> scoringMachine = new DefaultScoringMachine();
	private final CardHistoryRepository cardHistoryRepository;
	private final CardRepository cardRepository;
	private final MorphemeAnalyzer<NlpToken> morphemeAnalyzer;

	@Transactional
	public Score saveCardHistory(CardHistoryRequestDto cardHistoryRequestDto, Id solvedMemberId) {
		Card findCard = cardRepository.findByCardIdAndIsDeletedFalse(cardHistoryRequestDto.getCardId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다."));

		Score score = scoringMachine.giveScore(morphemeAnalyzer, findCard, cardHistoryRequestDto.getUserAnswer());
		CardHistory cardHistory = CardHistoryFactory.makeCardHistory(cardHistoryRequestDto, findCard, solvedMemberId,
			score);
		cardHistoryRepository.save(cardHistory);
		return score;
	}

	@Transactional(readOnly = true)
	public CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesByCardId(@NonNull Id cardId,
		int pageSize,
		Id lastCardHistoryId) {
		return cardHistoryRepository.findCardHistoriesByCardId(cardId, pageSize, lastCardHistoryId);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesBySolvedMemberId(
		@NonNull Id solvedMemberId,
		int pageSize,
		Id lastCardHistoryId) {
		return cardHistoryRepository.findCardHistoriesBySolvedMemberId(solvedMemberId, pageSize, lastCardHistoryId);
	}
}
