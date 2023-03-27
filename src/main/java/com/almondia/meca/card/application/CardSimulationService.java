package com.almondia.meca.card.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.service.CardPicker;
import com.almondia.meca.card.domain.service.RandomCardPicker;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardSimulationService {

	private final CategoryChecker categoryChecker;
	private final CardRepository cardRepository;

	@Transactional(readOnly = true)
	public List<CardResponseDto> simulateRandom(Id categoryId, Id memberId, int limit) {
		categoryChecker.checkAuthority(categoryId, memberId);
		List<Card> cards = cardRepository.findByCategoryId(categoryId);
		CardPicker cardPicker = new RandomCardPicker();
		List<Card> pick = cardPicker.pick(cards, limit);
		return pick.stream().map(CardMapper::cardToDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<CardResponseDto> simulateScore(Id categoryId, Id memberId, int limit) {
		categoryChecker.checkAuthority(categoryId, memberId);
		List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, limit);
		return cards.stream().map(CardMapper::cardToDto).collect(Collectors.toList());
	}
}
