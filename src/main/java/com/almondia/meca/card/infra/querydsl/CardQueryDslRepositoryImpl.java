package com.almondia.meca.card.infra.querydsl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.almondia.meca.card.application.helper.CardMapper;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CardQueryDslRepositoryImpl implements CardQueryDslRepository {

	private static final QCard card = QCard.card;
	private static final QCardHistory cardHistory = QCardHistory.cardHistory;
	private static final QMember member = QMember.member;
	private static final QCategory category = QCategory.category;

	private final JPAQueryFactory queryFactory;

	@Override
	public List<CardDto> findCardByCategoryId(
		int pageSize,
		@Nullable Id lastCardId,
		Id categoryId,
		CardSearchOption cardSearchOption
	) {
		return queryFactory.selectFrom(card)
			.where(
				card.categoryId.eq(categoryId),
				containTitle(cardSearchOption.getContainTitle()),
				card.isDeleted.eq(false),
				lessOrEqCardId(lastCardId)
			)
			.orderBy(card.cardId.uuid.desc())
			.limit(pageSize + 1L)
			.fetch()
			.stream()
			.map(CardMapper::cardToDto)
			.collect(Collectors.toList());
	}

	@Override
	public Optional<SharedCardResponseDto> findCardInSharedCategory(Id cardId) {
		SharedCardResponseDto sharedCardResponseDto = queryFactory.select(Projections.constructor(
				SharedCardResponseDto.class,
				card,
				member
			))
			.from(card)
			.innerJoin(member)
			.on(card.memberId.eq(member.memberId))
			.innerJoin(category)
			.on(card.categoryId.eq(category.categoryId))
			.where(card.cardId.eq(cardId)
				.and(card.isDeleted.eq(false))
				.and(category.isShared.eq(true)))
			.fetchOne();
		return Optional.ofNullable(sharedCardResponseDto);
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
	public Map<Id, Long> countCardsByCategoryIdIsDeletedFalse(List<Id> categoryIds) {
		Map<Id, Long> collect = queryFactory.select(card.categoryId, card.count())
			.from(card)
			.where(card.categoryId.in(categoryIds)
				, card.isDeleted.eq(false))
			.groupBy(card.categoryId)
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(card.categoryId),
				tuple -> {
					Long aLong = tuple.get(1, Long.class);
					if (aLong == null) {
						return 0L;
					}
					return aLong;
				}
			));
		for (Id categoryId : categoryIds) {
			collect.putIfAbsent(categoryId, 0L);
		}
		return collect;
	}

	@Override
	public List<Card> findCardByCategoryIdScoreAsc(Id categoryId, int limit) {
		SubQueryExpression<Double> scoreAvgByCardId = queryFactory
			.select(cardHistory.score.score.avg())
			.from(cardHistory)
			.where(cardHistory.cardId.eq(card.cardId));

		List<Tuple> query = queryFactory.select(card, scoreAvgByCardId)
			.from(card)
			.where(card.categoryId.eq(categoryId)
				.and(card.isDeleted.eq(false)))
			.limit(1000)
			.fetch();

		return query.stream()
			.sorted(Comparator.comparing(tuple -> {
				Double aDouble = tuple.get(1, Double.class);
				if (aDouble == null) {
					return 0.0;
				}
				return aDouble;
			}))
			.map(tuple -> tuple.get(card))
			.limit(limit)
			.collect(Collectors.toList());
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

	@Nullable
	private BooleanExpression lessOrEqCardId(@Nullable Id lastCardId) {
		return lastCardId == null ? null : card.cardId.uuid.loe(lastCardId.getUuid());
	}

	@Nullable
	private BooleanExpression containTitle(@Nullable String containTitle) {
		return containTitle == null ? null : card.title.title.contains(containTitle);
	}
}
