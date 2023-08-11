package com.almondia.meca.card.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.card.application.helper.CardFactory;
import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardCountAndShareResponseDto;
import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.CardWithStatisticsDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.service.CardChecker;
import com.almondia.meca.card.infra.querydsl.CardSearchOption;
import com.almondia.meca.cardhistory.controller.dto.CardStatisticsDto;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.repository.MemberRepository;
import com.almondia.meca.recommand.domain.repository.CategoryRecommendRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CardService {

	private final MemberRepository memberRepository;
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
	public CursorPage<CardWithStatisticsDto> searchCursorPagingCard(
		int pageSize,
		Id lastCardId,
		Id categoryId,
		Member member,
		CardSearchOption cardSearchOption
	) {
		// search
		Category category = categoryChecker.checkAuthority(categoryId, member.getMemberId());
		long likeCount = categoryRecommendRepository.countByCategoryIdAndIsDeletedFalse(categoryId);
		List<CardDto> collect = cardRepository.findCardByCategoryId(pageSize,
			lastCardId, categoryId, cardSearchOption);
		Map<Id, Pair<Double, Long>> avgAndCountsByCardIds = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCardIds(
			collect.stream().map(CardDto::getCardId).collect(Collectors.toList()));

		// combine
		List<CardWithStatisticsDto> contents = collect.stream()
			.map(cardDto -> new CardWithStatisticsDto(cardDto, new CardStatisticsDto(
				avgAndCountsByCardIds.getOrDefault(cardDto.getCardId(), Pair.of(0.0, 0L)).getFirst(),
				avgAndCountsByCardIds.getOrDefault(cardDto.getCardId(), Pair.of(0.0, 0L)).getSecond()
			)))
			.collect(Collectors.toList());
		CursorPage<CardWithStatisticsDto> cursor = CursorPage.<CardWithStatisticsDto>builder()
			.lastIdExtractStrategy(cardWithStatisticsDto -> cardWithStatisticsDto.getCard().getCardId())
			.contents(contents)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
		CardCursorPageWithCategory result = new CardCursorPageWithCategory(cursor);
		result.setCategory(category);
		result.setCategoryLikeCount(likeCount);
		result.setMember(member);
		return result;
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
	public CardResponseDto findSharedCard(Id cardId) {
		return cardRepository.findCardInSharedCategory(cardId)
			.orElseThrow(() -> new IllegalArgumentException("공유된 카테고리의 카드가 존재하지 않습니다"));
	}

	@Transactional(readOnly = true)
	public CardCountAndShareResponseDto findCardsCountAndSharedByCategoryId(Id categoryId, Id memberId) {
		Category category = categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다"));
		if (!category.isMyCategory(memberId) && !category.isShared()) {
			throw new AccessDeniedException("공유되지 않은 카테고리에 접근할 수 없습니다");
		}
		long count = cardRepository.countCardsByCategoryId(categoryId);
		return new CardCountAndShareResponseDto(count, category.isShared());
	}

	@Transactional(readOnly = true)
	public CardCursorPageWithCategory searchCursorPagingSharedCard(
		int pageSize,
		Id lastCardId,
		Id categoryId,
		CardSearchOption cardSearchOption
	) {
		// search
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다"));
		if (category.isDeleted() || !category.isShared()) {
			throw new IllegalArgumentException("공유되지 않은 카테고리에 접근할 수 없습니다");
		}
		Member member = memberRepository.findById(category.getMemberId())
			.orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다"));
		if (member.isDeleted()) {
			throw new IllegalArgumentException("삭제된 멤버에 접근할 수 없습니다");
		}
		long likeCount = categoryRecommendRepository.countByCategoryIdAndIsDeletedFalse(categoryId);
		List<CardDto> cards = cardRepository.findCardByCategoryId(pageSize, lastCardId, categoryId, cardSearchOption);
		Map<Id, Pair<Double, Long>> avgAndCountsByCardIds = cardHistoryRepository.findCardHistoryScoresAvgAndCountsByCardIds(
			cards.stream().map(CardDto::getCardId).collect(Collectors.toList()));
		List<CardWithStatisticsDto> contents = cards.stream()
			.map(cardDto -> new CardWithStatisticsDto(cardDto, new CardStatisticsDto(
				avgAndCountsByCardIds.getOrDefault(cardDto.getCardId(), Pair.of(0.0, 0L)).getFirst(),
				avgAndCountsByCardIds.getOrDefault(cardDto.getCardId(), Pair.of(0.0, 0L)).getSecond()
			)))
			.collect(Collectors.toList());

		// combine
		CursorPage<CardWithStatisticsDto> cursor = CursorPage.<CardWithStatisticsDto>builder()
			.lastIdExtractStrategy(cardWithStatisticsDto -> cardWithStatisticsDto.getCard().getCardId())
			.contents(contents)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
		CardCursorPageWithCategory result = new CardCursorPageWithCategory(cursor);

		result.setCategory(category);
		result.setCategoryLikeCount(likeCount);
		result.setMember(member);
		return result;
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
		if (updateCardRequestDto.getAnswer() != null) {
			card.changeAnswer(updateCardRequestDto.getAnswer());
		}
	}
}