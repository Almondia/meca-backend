package com.almondia.meca.cardhistory.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CardHistoryQueryDslRepositoryImpl implements CardHistoryQueryDslRepository {

	private static final QCardHistory cardHistory = QCardHistory.cardHistory;
	private static final QCard card = QCard.card;
	private static final QCategory category = QCategory.category;
	private static final QMember member = QMember.member;

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesByCardId(@NonNull Id cardId,
		int pageSize, Id lastCardHistoryId) {
		Assert.isTrue(pageSize >= 0, "pageSize must be greater than or equal to 0");
		Assert.isTrue(pageSize <= 1000, "pageSize must be less than or equal to 1000");

		List<CardHistoryWithCardAndMemberResponseDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryWithCardAndMemberResponseDto.class, cardHistory, cardHistory.cardId,
					member.memberId, member.name, cardHistory.cardSnapShot))
			.from(cardHistory)
			.innerJoin(member)
			.on(cardHistory.solvedMemberId.eq(member.memberId), member.isDeleted.eq(false))
			.where(cardHistory.isDeleted.eq(false), cardHistory.cardId.eq(cardId),
				lessOrEqCardHistoryId(lastCardHistoryId))
			.orderBy(cardHistory.cardHistoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();

		Id hasNext = null;
		if (contents.size() > pageSize) {
			hasNext = contents.get(pageSize).getCardHistory().getCardHistoryId();
			contents.remove(pageSize);
		}

		return new CursorPage<>(contents, hasNext, pageSize, SortOrder.DESC);
	}

	@Override
	public CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesBySolvedMemberId(
		@NonNull Id solvedMemberId, int pageSize, Id lastCardHistoryId) {
		Assert.isTrue(pageSize >= 0, "pageSize must be greater than or equal to 0");
		Assert.isTrue(pageSize <= 1000, "pageSize must be less than or equal to 1000");

		List<CardHistoryWithCardAndMemberResponseDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryWithCardAndMemberResponseDto.class, cardHistory, cardHistory.cardId,
					member.memberId, member.name, cardHistory.cardSnapShot))
			.from(cardHistory)
			.innerJoin(member)
			.on(cardHistory.solvedMemberId.eq(member.memberId), member.isDeleted.eq(false))
			.where(cardHistory.solvedMemberId.eq(solvedMemberId), cardHistory.isDeleted.eq(false),
				lessOrEqCardHistoryId(lastCardHistoryId))
			.orderBy(cardHistory.cardHistoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();

		Id hasNext = null;
		if (contents.size() > pageSize) {
			hasNext = contents.get(pageSize).getCardHistory().getCardHistoryId();
			contents.remove(pageSize);
		}
		return new CursorPage<>(contents, hasNext, pageSize, SortOrder.DESC);
	}

	@Override
	public Map<Id, Pair<Double, Long>> findCardHistoryScoresAvgAndCountsByCategoryIds(List<Id> categoryIds) {
		Map<Id, Pair<Double, Long>> collect = jpaQueryFactory.select(card.categoryId, cardHistory.score.score.avg(),
				cardHistory.cardId.countDistinct())
			.from(cardHistory)
			.innerJoin(card)
			.on(cardHistory.cardId.eq(card.cardId),
				card.isDeleted.eq(false)
			)
			.where(card.categoryId.in(categoryIds), cardHistory.isDeleted.eq(false))
			.groupBy(card.categoryId)
			.fetch()
			.stream()
			.collect(Collectors.toMap(tuple -> tuple.get(card.categoryId), tuple -> {
				Double avg = tuple.get(cardHistory.score.score.avg());
				avg = avg == null ? 0.0 : avg;
				Long count = tuple.get(cardHistory.cardId.countDistinct());
				count = count == null ? 0L : count;
				return Pair.of(avg, count);
			}));
		for (Id categoryId : categoryIds) {
			collect.putIfAbsent(categoryId, Pair.of(0.0, 0L));
		}
		return collect;
	}

	private BooleanExpression lessOrEqCardHistoryId(Id lastCardHistoryId) {
		return lastCardHistoryId == null ? null : cardHistory.cardHistoryId.uuid.loe(lastCardHistoryId.getUuid());
	}
}
