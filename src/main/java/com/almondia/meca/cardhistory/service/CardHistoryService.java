package com.almondia.meca.cardhistory.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
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

	private List<CardHistory> getCardHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto,
		Map<Id, List<Id>> categoryIdsByCardId) {
		List<CardHistory> cardHistories = new ArrayList<>();
		for (CardHistoryDto cardHistoryDto : saveRequestCardHistoryDto.getCardHistoryDtos()) {
			CardHistory cardHistory = CardHistory.builder()
				.cardHistoryId(Id.generateNextId())
				.cardId(cardHistoryDto.getCardId())
				.categoryId(categoryIdsByCardId.get(cardHistoryDto.getCardId()).get(0))
				.userAnswer(cardHistoryDto.getUserAnswer())
				.score(cardHistoryDto.getScore())
				.build();
			cardHistories.add(cardHistory);
		}
		return cardHistories;
	}

	private Map<Id, List<Id>> checkAuthority(SaveRequestCardHistoryDto saveRequestCardHistoryDto, Id memberId) {
		List<Id> cardIds = saveRequestCardHistoryDto.getCardHistoryDtos().stream()
			.map(CardHistoryDto::getCardId)
			.collect(Collectors.toList());
		Map<Id, List<Id>> categoryIdsByCardId = cardRepository.findMapByListOfCardIdAndMemberId(cardIds, memberId);
		long allValuesCount = categoryIdsByCardId.values().stream().mapToLong(Collection::size).sum();
		if (allValuesCount != cardIds.size()) {
			throw new IllegalArgumentException("권한이 없거나 옳바르지 않은 입력을 하셨습니다");
		}
		return categoryIdsByCardId;
	}
}
