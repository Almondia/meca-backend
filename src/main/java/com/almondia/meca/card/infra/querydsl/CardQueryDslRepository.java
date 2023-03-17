package com.almondia.meca.card.infra.querydsl;

import java.util.List;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.common.infra.querydsl.SortOption;

public interface CardQueryDslRepository {

	List<Card> findCardByCategoryIdUsingCursorPaging(int pageSize,
		CardSearchCriteria criteria, SortOption<CardSortField> sortOption);
}
