package com.almondia.meca.cardhistory.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryRequestDto;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryResponseDto;
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
	public void saveHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto, Id memberId) {
		Map<Id, List<Id>> categoryIdsByCardId = checkAuthority(saveRequestCardHistoryDto, memberId);
		List<CardHistory> cardHistories = getCardHistories(saveRequestCardHistoryDto, categoryIdsByCardId);
		cardHistoryRepository.saveAll(cardHistories);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardHistoryResponseDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize,
		Id lastCardHistoryId) {
		return cardHistoryRepository.findCardHistoriesByCardId(cardId, pageSize, lastCardHistoryId);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardHistoryResponseDto> findCardHistoriesByCategoryId(@NonNull Id categoryId, int pageSize,
		Id lastCardHistoryId) {
		return cardHistoryRepository.findCardHistoriesByCategoryId(categoryId, pageSize, lastCardHistoryId);
	}

	private List<CardHistory> getCardHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto,
		Map<Id, List<Id>> categoryIdsByCardId) {
		List<CardHistory> cardHistories = new ArrayList<>();
		for (CardHistoryRequestDto cardHistoryResponseDto : saveRequestCardHistoryDto.getCardHistories()) {
			CardHistory cardHistory = CardHistory.builder()
				.cardHistoryId(Id.generateNextId())
				.cardId(cardHistoryResponseDto.getCardId())
				.categoryId(categoryIdsByCardId.get(cardHistoryResponseDto.getCardId()).get(0))
				.userAnswer(cardHistoryResponseDto.getUserAnswer())
				.score(cardHistoryResponseDto.getScore())
				.build();
			cardHistories.add(cardHistory);
		}
		return cardHistories;
	}

	private Map<Id, List<Id>> checkAuthority(SaveRequestCardHistoryDto saveRequestCardHistoryDto, Id memberId) {
		List<Id> cardIds = saveRequestCardHistoryDto.getCardHistories().stream()
			.map(CardHistoryRequestDto::getCardId)
			.collect(Collectors.toList());
		Map<Id, List<Id>> categoryIdsByCardId = cardRepository.findMapByListOfCardIdAndMemberId(cardIds, memberId);
		long allValuesCount = categoryIdsByCardId.values().stream().mapToLong(Collection::size).sum();
		if (allValuesCount != cardIds.size()) {
			throw new IllegalArgumentException("권한이 없거나 옳바르지 않은 입력을 하셨습니다");
		}
		return categoryIdsByCardId;
	}
}
