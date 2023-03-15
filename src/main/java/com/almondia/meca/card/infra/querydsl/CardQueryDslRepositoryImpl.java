package com.almondia.meca.card.infra.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.common.infra.querydsl.SortFactory;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CardQueryDslRepositoryImpl implements CardQueryDslRepository {

	private static final QCard card = QCard.card;
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Card> findCardByCategoryIdUsingCursorPaging(
		int pageSize,
		CardSearchCriteria criteria,
		SortOption<CardSortField> sortOption
	) {
		return queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.orderBy(SortFactory.createOrderSpecifier(sortOption))
			.limit(pageSize)
			.fetch();
	}
}
