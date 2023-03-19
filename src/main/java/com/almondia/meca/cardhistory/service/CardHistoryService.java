package com.almondia.meca.cardhistory.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.repository.CardRepository;
import com.almondia.meca.card.sevice.checker.CardChecker;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.cardhistory.service.helper.CardHistoryFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardHistoryService {

	private final CardHistoryRepository cardHistoryRepository;
	private final CardRepository cardRepository;
	private final CardChecker cardChecker;

	@Transactional
	public void saveHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto) {
		List<CardHistory> cardHistories = new ArrayList<>();
		for (CardHistoryDto cardHistoryDto : saveRequestCardHistoryDto.getCardHistoryDtos()) {
			Card card = cardRepository.findById(cardHistoryDto.getCardId())
				.orElseThrow(() -> new IllegalArgumentException("해당 카드가 존재하지 않습니다"));
			cardHistories.add(CardHistoryFactory.makeCardHistory(cardHistoryDto, card.getCategoryId()));
		}
		cardHistoryRepository.saveAll(cardHistories);
	}
}
