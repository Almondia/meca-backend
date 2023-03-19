package com.almondia.meca.card.infra.querydsl;

import java.util.List;
import java.util.Map;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;

public interface CardQueryDslRepository {

	List<Card> findCardByCategoryIdUsingCursorPaging(int pageSize,
		CardSearchCriteria criteria, SortOption<CardSortField> sortOption);

	Map<Id, List<Id>> findMapByListOfCardIdAndMemberId(List<Id> cardIds, Id memberId);
}
