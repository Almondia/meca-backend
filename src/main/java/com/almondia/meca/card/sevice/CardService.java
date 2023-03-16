package com.almondia.meca.card.sevice;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.infra.querydsl.CardSearchCriteria;
import com.almondia.meca.card.infra.querydsl.CardSortField;
import com.almondia.meca.card.repository.CardRepository;
import com.almondia.meca.card.repository.KeywordCardRepository;
import com.almondia.meca.card.repository.MultiChoiceCardRepository;
import com.almondia.meca.card.repository.OxCardRepository;
import com.almondia.meca.card.sevice.checker.CardChecker;
import com.almondia.meca.card.sevice.helper.CardFactory;
import com.almondia.meca.card.sevice.helper.CardMapper;
import com.almondia.meca.category.service.checker.CategoryChecker;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CardService {

	private final CardRepository cardRepository;
	private final OxCardRepository oxCardRepository;
	private final KeywordCardRepository keywordCardRepository;
	private final MultiChoiceCardRepository multiChoiceCardRepository;
	private final CategoryChecker categoryChecker;
	private final CardChecker cardChecker;

	@Transactional
	public CardResponseDto saveCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		Card card = CardFactory.genCard(saveCardRequestDto, memberId);
		if (card instanceof OxCard) {
			OxCard oxCard = oxCardRepository.save((OxCard)card);
			return CardMapper.oxCardToDto(oxCard);
		}
		if (card instanceof KeywordCard) {
			KeywordCard keywordCard = keywordCardRepository.save((KeywordCard)card);
			return CardMapper.keywordCardToDto(keywordCard);
		}
		if (card instanceof MultiChoiceCard) {
			MultiChoiceCard multiChoiceCard = multiChoiceCardRepository.save((MultiChoiceCard)card);
			return CardMapper.multiChoiceCardToDto(multiChoiceCard);
		}
		throw new IllegalArgumentException("지원하는 카드 유형이 아닙니다");
	}

	@Transactional
	public CardResponseDto updateCard(UpdateCardRequestDto updateCardRequestDto, Id cardId, Id memberId) {
		Card card = cardChecker.checkAuthority(cardId, memberId, updateCardRequestDto.getCardType());
		updateCard(updateCardRequestDto, memberId, card);
		if (card instanceof OxCard) {
			return CardMapper.oxCardToDto((OxCard)card);
		}
		if (card instanceof KeywordCard) {
			return CardMapper.keywordCardToDto((KeywordCard)card);
		}
		if (card instanceof MultiChoiceCard) {
			return CardMapper.multiChoiceCardToDto((MultiChoiceCard)card);
		}
		throw new IllegalArgumentException("지원하는 카드 유형이 아닙니다");
	}

	@Transactional(readOnly = true)
	public CardResponseDto searchCursorPagingCard(
		int pageSize,
		CardSearchCriteria cardSearchCriteria,
		SortOption<CardSortField> sortOption) {
		List<Card> cursor = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize,
			cardSearchCriteria, sortOption);
		return null;
	}

	private void updateCard(UpdateCardRequestDto updateCardRequestDto, Id memberId, Card card) {
		if (updateCardRequestDto.getTitle() != null) {
			card.changeTitle(updateCardRequestDto.getTitle());
		}
		if (updateCardRequestDto.getImages() != null) {
			card.changeImages(updateCardRequestDto.getImages());
		}
		if (updateCardRequestDto.getQuestion() != null) {
			card.changeQuestion(updateCardRequestDto.getQuestion());
		}
		if (updateCardRequestDto.getCategoryId() != null) {
			categoryChecker.checkAuthority(updateCardRequestDto.getCategoryId(), memberId);
			card.changeCategoryId(updateCardRequestDto.getCategoryId());
		}
	}
}