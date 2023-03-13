package com.almondia.meca.card.sevice.checker;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.repository.KeywordCardRepository;
import com.almondia.meca.card.repository.MultiChoiceCardRepository;
import com.almondia.meca.card.repository.OxCardRepository;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardChecker {

	private final KeywordCardRepository keywordCardRepository;
	private final OxCardRepository oxCardRepository;
	private final MultiChoiceCardRepository multiChoiceCardRepository;

	public Card checkAuthority(Id cardId, Id memberId, CardType cardType) {
		if (cardType.equals(CardType.OX_QUIZ)) {
			return oxCardRepository.findByCardIdAndMemberId(cardId, memberId)
				.orElseThrow(() -> new AccessDeniedException(String.format("%s 유저는 해당 카드를 수정할 권한이 없습니다", memberId)));
		}
		if (cardType.equals(CardType.KEYWORD)) {
			return keywordCardRepository.findByCardIdAndMemberId(cardId, memberId)
				.orElseThrow(() -> new AccessDeniedException(String.format("%s 유저는 해당 카드를 수정할 권한이 없습니다", memberId)));
		}
		return multiChoiceCardRepository.findByCardIdAndMemberId(cardId, memberId)
			.orElseThrow(() -> new AccessDeniedException(String.format("%s 유저는 해당 카드를 수정할 권한이 없습니다", memberId)));
	}
}
