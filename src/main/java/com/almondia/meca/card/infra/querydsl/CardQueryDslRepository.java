package com.almondia.meca.card.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardCursorPageWithSharedCategoryDto;
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.common.domain.vo.Id;

import lombok.NonNull;

public interface CardQueryDslRepository {

	CardCursorPageWithCategory findCardByCategoryIdUsingCursorPaging(
		int pageSize, Id lastCardId, @NonNull Id categoryId, CardSearchOption cardSearchOption);

	CardCursorPageWithSharedCategoryDto findCardBySharedCategoryCursorPaging(
		int pageSize, Id lastCardId, @NonNull Id categoryId, CardSearchOption cardSearchOption);

	Optional<SharedCardResponseDto> findCardInSharedCategory(Id cardId);

	long countCardsByCategoryId(Id categoryId);

	Map<Id, Long> countCardsByCategoryIdIsDeletedFalse(List<Id> categoryIds);

	List<Card> findCardByCategoryIdScoreAsc(Id categoryId, int limit);

	Map<Id, List<Id>> findMapByListOfCardIdAndMemberId(List<Id> cardIds, Id memberId);
}
