package com.almondia.meca.card.application;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.application.helper.CardFactory;
import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardCursorPageWithSharedCategoryDto;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.service.CardChecker;
import com.almondia.meca.card.infra.querydsl.CardSearchOption;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CardService {

	private final CardHistoryRepository cardHistoryRepository;
	private final CardRepository cardRepository;
	private final CategoryRepository categoryRepository;
	private final CategoryChecker categoryChecker;
	private final CardChecker cardChecker;
	private final CategoryRecommendRepository categoryRecommendRepository;

	@Transactional
	public CardDto saveCard(SaveCardRequestDto saveCardRequestDto, Id memberId) {
		Card card = CardFactory.genCard(saveCardRequestDto, memberId);
		Card savedCard = cardRepository.save(card);
		return CardMapper.cardToDto(savedCard);
	}

	@Transactional
	public CardDto updateCard(UpdateCardRequestDto updateCardRequestDto, Id cardId, Id memberId) {
		Card card = cardChecker.checkAuthority(cardId, memberId);
		updateCard(updateCardRequestDto, memberId, card);
		return CardMapper.cardToDto(card);
	}

	@Transactional(readOnly = true)
	public CursorPage<CardDto> searchCursorPagingCard(
		int pageSize,
		Id lastCardId,
		Id categoryId,
		Id memberId,
		CardSearchOption cardSearchOption
	) {
		Category category = categoryChecker.checkAuthority(categoryId, memberId);
		long likeCount = categoryRecommendRepository.countByCategoryIdAndIsDeletedFalse(categoryId);
		CardCursorPageWithCategory cursor = cardRepository.findCardByCategoryIdUsingCursorPaging(pageSize,
			lastCardId, categoryId, cardSearchOption);
		cursor.setCategory(category);
		cursor.setCategoryLikeCount(likeCount);
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
	public CardDto findCardById(Id cardId, Id memberId) {
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
		Category category = categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다"));
		if (!category.isMyCategory(memberId) && !category.isShared()) {
			throw new AccessDeniedException("공유되지 않은 카테고리에 접근할 수 없습니다");
		}
		return cardRepository.countCardsByCategoryId(categoryId);
	}

	@Transactional(readOnly = true)
	public CardCursorPageWithSharedCategoryDto searchCursorPagingSharedCard(
		int pageSize,
		Id lastCardId,
		Id categoryId,
		CardSearchOption cardSearchOption
	) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다"));
		if (!category.isShared()) {
			throw new AccessDeniedException("공유되지 않은 카테고리에 접근할 수 없습니다");
		}
		long likeCount = categoryRecommendRepository.countByCategoryIdAndIsDeletedFalse(categoryId);
		CardCursorPageWithSharedCategoryDto cursor = cardRepository.findCardBySharedCategoryCursorPaging(pageSize,
			lastCardId, categoryId, cardSearchOption);
		cursor.setCategory(category);
		cursor.setCategoryLikeCount(likeCount);
		return cursor;
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
}