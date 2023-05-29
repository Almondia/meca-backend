package com.almondia.meca.cardhistory.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.application.helper.CardHistoryFactory;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardHistoryService {

	private final CardHistoryRepository cardHistoryRepository;
	private final CardRepository cardRepository;

	@Transactional
	public void saveCardHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto, Id solvedMemberId) {
		List<Id> cardIds = saveRequestCardHistoryDto.getCardHistories().stream()
			.map(CardHistoryRequestDto::getCardId)
			.collect(Collectors.toList());
		if (cardRepository.countByIsDeletedFalseAndCardIdIn(cardIds) != cardIds.size()) {
			throw new IllegalArgumentException("존재하지 않는 카드가 포함되어 있습니다.");
		}
		List<CardHistory> cardHistories = CardHistoryFactory.makeCardHistories(saveRequestCardHistoryDto,
			solvedMemberId);
		cardHistoryRepository.saveAll(cardHistories);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardHistoryDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize,
		Id lastCardHistoryId) {
		return cardHistoryRepository.findCardHistoriesByCardId(cardId, pageSize, lastCardHistoryId);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardHistoryDto> findCardHistoriesByCategoryId(@NonNull Id categoryId, int pageSize,
		Id lastCardHistoryId) {
		return cardHistoryRepository.findCardHistoriesByCategoryId(categoryId, pageSize, lastCardHistoryId);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardHistoryDto> findCardHistoriesBySolvedMemberId(@NonNull Id solvedMemberId,
		int pageSize,
		Id lastCardHistoryId) {
		return cardHistoryRepository.findCardHistoriesBySolvedMemberId(solvedMemberId, pageSize, lastCardHistoryId);
	}
}
