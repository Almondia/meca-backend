package com.almondia.meca.card.sevice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.repository.KeywordCardRepository;
import com.almondia.meca.card.repository.MultiChoiceCardRepository;
import com.almondia.meca.card.repository.OxCardRepository;
import com.almondia.meca.card.sevice.checker.CardChecker;
import com.almondia.meca.card.sevice.helper.CardFactory;
import com.almondia.meca.card.sevice.helper.CardMapper;
import com.almondia.meca.category.service.checker.CategoryChecker;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CardService {

	private final OxCardRepository oxCardRepository;
	private final KeywordCardRepository keywordCardRepository;
	private final MultiChoiceCardRepository multiChoiceCardRepository;
	private final CategoryChecker categoryChecker;
	private final CardChecker cardChecker;

	@Transactional
	public CardResponseDto saveCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		Card card = CardFactory.genCard(saveCardRequestDto, memberId);
		return saveCardByCardType(card);
	}

	@Transactional
	public CardResponseDto updateCard(UpdateCardRequestDto updateCardRequestDto, Id cardId, Id memberId) {
		categoryChecker.checkAuthority(updateCardRequestDto.getCategoryId(), memberId);
		Card card = cardChecker.checkAuthority(cardId, memberId, updateCardRequestDto.getCardType());
		return saveCardByCardType(card);
	}

	private CardResponseDto saveCardByCardType(Card card) {
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
}