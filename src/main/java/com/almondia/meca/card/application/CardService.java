package com.almondia.meca.card.application;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.application.helper.CardFactory;
import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.service.CardChecker;
import com.almondia.meca.card.infra.querydsl.CardSearchCriteria;
import com.almondia.meca.card.infra.querydsl.CardSortField;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CardService {

	private final CardHistoryRepository cardHistoryRepository;
	private final CardRepository cardRepository;
	private final CategoryRepository categoryRepository;
	private final CategoryChecker categoryChecker;
	private final CardChecker cardChecker;

	@Transactional
	public CardResponseDto saveCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		Card card = CardFactory.genCard(saveCardRequestDto, memberId);
		Card savedCard = cardRepository.save(card);
		return CardMapper.cardToDto(savedCard);
	}

	@Transactional
	public CardResponseDto updateCard(UpdateCardRequestDto updateCardRequestDto, Id cardId, Id memberId) {
		Card card = cardChecker.checkAuthority(cardId, memberId);
		updateCard(updateCardRequestDto, memberId, card);
		return CardMapper.cardToDto(card);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardResponseDto> searchCursorPagingCard(
		int pageSize,
		Id categoryId,
		CardSearchCriteria cardSearchCriteria,
		SortOption<CardSortField> sortOption,
		Id memberId
	) {
		Category category = categoryChecker.checkAuthority(categoryId, memberId);
		CardCursorPageWithCategory cursor = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize,
			cardSearchCriteria, sortOption);
		cursor.setCategory(category);
		return cursor;
	}

	@Transactional
	public void deleteCard(Id cardId, Id memberId) {
		Card card = cardChecker.checkAuthority(cardId, memberId);
		card.delete();
		List<CardHistory> cardHistories = cardHistoryRepository.findByCardId(cardId);
		cardHistories.forEach(CardHistory::delete);
	}

	@Transactional(readOnly = true)
	public CardResponseDto findCardById(Id cardId, Id memberId) {
		Card card = cardChecker.checkAuthority(cardId, memberId);
		if (card.isDeleted()) {
			throw new IllegalArgumentException("삭제된 카드입니다");
		}
		return CardMapper.cardToDto(card);
	}

	@Transactional(readOnly = true)
	public SharedCardResponseDto findSharedCard(Id cardId) {
		return cardRepository.findCardInSharedCategory(cardId)
			.orElseThrow(() -> new IllegalArgumentException("공유된 카테고리의 카드가 존재하지 않습니다"));
	}

	@Transactional(readOnly = true)
	public long findCardsCountByCategoryId(Id categoryId, Id memberId) {
		categoryChecker.checkAuthority(categoryId, memberId);
		return cardRepository.countCardsByCategoryId(categoryId);
	}

	private void updateCard(UpdateCardRequestDto updateCardRequestDto, Id memberId, Card card) {
		if (updateCardRequestDto.getTitle() != null) {
			card.changeTitle(updateCardRequestDto.getTitle());
		}
		if (updateCardRequestDto.getDescription() != null) {
			card.changeEditText(updateCardRequestDto.getDescription());
		}
		if (updateCardRequestDto.getQuestion() != null) {
			card.changeQuestion(updateCardRequestDto.getQuestion());
		}
		if (updateCardRequestDto.getCategoryId() != null) {
			categoryChecker.checkAuthority(updateCardRequestDto.getCategoryId(), memberId);
			card.changeCategoryId(updateCardRequestDto.getCategoryId());
		}
	}

	@Transactional(readOnly = true)
	public CursorPage<CardResponseDto> searchCursorPagingSharedCard(int pageSize, Id categoryId,
		CardSearchCriteria criteria, SortOption<CardSortField> sortOption
	) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다"));
		if (!category.isShared()) {
			throw new AccessDeniedException("공유되지 않은 카테고리에 접근할 수 없습니다");
		}
		CardCursorPageWithCategory cursor = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize,
			criteria, sortOption);
		cursor.setCategory(category);
		return cursor;
	}
}