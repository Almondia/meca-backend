package com.almondia.meca.card.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardCountGroupByScoreDto;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.service.CardPicker;
import com.almondia.meca.card.domain.service.RandomCardPicker;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardSimulationService {

	private final CardRepository cardRepository;
	private final CardHistoryRepository cardHistoryRepository;
	private final CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public List<CardDto> simulateRandom(Id categoryId, Id memberId, int limit) {
		Category category = categoryRepository.findByCategoryIdAndIsDeleted(categoryId, false)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다"));
		CardPicker cardPicker = new RandomCardPicker();
		if (category.isMyCategory(memberId) || (!category.isMyCategory(memberId) && category.isShared())) {
			List<Card> cards = cardRepository.findByCategoryIdAndIsDeleted(categoryId, false);
			List<Card> pick = cardPicker.pick(cards, limit);
			return pick.stream().map(CardMapper::cardToDto).collect(Collectors.toList());
		}
		throw new AccessDeniedException("해당 카테고리는 접근할 수 없는 카테고리입니다");
	}

	@Transactional(readOnly = true)
	public List<CardDto> simulateScore(Id categoryId, Id memberId, Score score, int limit) {
		Category category = categoryRepository.findByCategoryIdAndIsDeleted(categoryId, false)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다"));
		if (category.isMyCategory(memberId) || (!category.isMyCategory(memberId) && category.isShared())) {
			List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, score, limit);
			return cards.stream().map(CardMapper::cardToDto).collect(Collectors.toList());
		}
		throw new AccessDeniedException("해당 카테고리는 접근할 수 없는 카테고리입니다");
	}

	@Transactional(readOnly = true)
	public List<CardCountGroupByScoreDto> findCardCountByScore(Id categoryId) {
		if (!categoryRepository.existsByCategoryIdAndIsDeletedFalse(categoryId)) {
			throw new IllegalArgumentException("존재하지 않는 카테고리입니다");
		}
		List<Card> cards = cardRepository.findByCategoryIdAndIsDeleted(categoryId, false);
		Map<Id, Double> cardScores = cardHistoryRepository.findCardScoreAvgMapByCategoryId(categoryId);
		Map<Double, Long> counts = cardScores.entrySet().stream()
			.collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.counting()));
		for (Card card : cards) {
			if (!cardScores.containsKey(card.getCardId())) {
				counts.put(0.0, counts.getOrDefault(0.0, 0L) + 1);
			}
		}
		return counts.entrySet().stream()
			.map(entry -> new CardCountGroupByScoreDto(entry.getKey(), entry.getValue()))
			.sorted((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()))
			.collect(Collectors.toList());
	}
}
