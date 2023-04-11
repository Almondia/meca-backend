package com.almondia.meca.card.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortFactory;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.member.domain.entity.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CardQueryDslRepositoryImpl implements CardQueryDslRepository {

	private static final QCard card = QCard.card;
	private static final QCardHistory cardHistory = QCardHistory.cardHistory;
	private static final QMember member = QMember.member;

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

	@Override
	public Map<Id, List<Id>> findMapByListOfCardIdAndMemberId(List<Id> cardIds, Id memberId) {
		return queryFactory
			.select(card.cardId, card.categoryId)
			.from(card)
			.where(card.cardId.in(cardIds)
				.and(card.memberId.eq(memberId)))
			.fetch()
			.stream()
			.collect(Collectors.groupingBy(
				tuple -> tuple.get(card.cardId),
				Collectors.mapping(tuple -> tuple.get(card.categoryId), Collectors.toList())
			));
	}

	@Override
	public List<Card> findCardByCategoryIdScoreAsc(Id categoryId, int limit) {
		return queryFactory.selectFrom(card)
			.leftJoin(cardHistory).on(card.cardId.eq(cardHistory.cardId))
			.groupBy(card)
			.orderBy(cardHistory.score.score.avg().asc())
			.limit(limit)
			.fetch();
	}

	@Override
	public long countCardsByCategoryId(Id categoryId) {
		Long count = queryFactory.select(card.count())
			.from(card)
			.where(card.isDeleted.eq(false)
				.and(card.categoryId.eq(categoryId)))
			.fetchOne();
		return count == null ? 0 : count;
	}

	@Override
	public SharedCardResponseDto findSharedCard(Id cardId) {
		return queryFactory.select(Projections.constructor(
				SharedCardResponseDto.class,
				card,
				member
			))
			.from(card)
			.innerJoin(member)
			.on(card.memberId.eq(member.memberId))
			.where(card.cardId.eq(cardId))
			.fetchOne();
	}
}
