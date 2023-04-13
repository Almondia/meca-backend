package com.almondia.meca.card.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.almondia.meca.card.controller.dto.CardCursorPageWithCategory;
import com.almondia.meca.card.controller.dto.CardCursorPageWithSharedCategoryDto;
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;

public interface CardQueryDslRepository {

	CardCursorPageWithCategory findCardByCategoryIdUsingCursorPaging(int pageSize,
		CardSearchCriteria criteria, SortOption<CardSortField> sortOption);

	CardCursorPageWithSharedCategoryDto findCardBySharedCategoryCursorPaging(int pageSize,
		CardSearchCriteria criteria, SortOption<CardSortField> sortOption);

	Optional<SharedCardResponseDto> findCardInSharedCategory(Id cardId);

	long countCardsByCategoryId(Id categoryId);

	List<Card> findCardByCategoryIdScoreAsc(Id categoryId, int limit);

	Map<Id, List<Id>> findMapByListOfCardIdAndMemberId(List<Id> cardIds, Id memberId);
}
