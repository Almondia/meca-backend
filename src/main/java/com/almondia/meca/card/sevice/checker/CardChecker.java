package com.almondia.meca.card.sevice.checker;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardChecker {

	private final CardRepository cardRepository;

	public Card checkAuthority(Id cardId, Id memberId) {
		return cardRepository.findByCardIdAndMemberId(cardId, memberId)
			.orElseThrow(() -> new AccessDeniedException(String.format("%s 유저는 해당 카드를 수정할 권한이 없습니다", memberId)));
	}
}
