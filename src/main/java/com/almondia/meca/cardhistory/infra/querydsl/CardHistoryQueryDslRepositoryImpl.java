package com.almondia.meca.cardhistory.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.controller.dto.CardStatisticsDto;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CardHistoryQueryDslRepositoryImpl implements CardHistoryQueryDslRepository {

	private static final QCardHistory cardHistory = QCardHistory.cardHistory;
	private static final QCard card = QCard.card;
	private static final QMember member = QMember.member;

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesByCardId(Id cardId,
		int pageSize, @Nullable Id lastCardHistoryId) {
		List<CardHistoryWithCardAndMemberResponseDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryWithCardAndMemberResponseDto.class, cardHistory, cardHistory.cardId,
					member.memberId, member.name, cardHistory.cardSnapShot))
			.from(cardHistory)
			.innerJoin(member)
			.on(cardHistory.solvedMemberId.eq(member.memberId), member.isDeleted.eq(false))
			.where(cardHistory.isDeleted.eq(false), cardHistory.cardId.eq(cardId),
				lessOrEqCardHistoryId(lastCardHistoryId))
			.orderBy(cardHistory.cardHistoryId.tsid.desc())
			.limit(pageSize + 1L)
			.fetch();

		return CursorPage.<CardHistoryWithCardAndMemberResponseDto>builder()
			.lastIdExtractStrategy(cardHistoryDto -> cardHistoryDto.getCardHistory().getCardHistoryId())
			.contents(contents)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
	}

	@Override
	public CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesBySolvedMemberId(
		Id solvedMemberId, int pageSize, @Nullable Id lastCardHistoryId) {
		List<CardHistoryWithCardAndMemberResponseDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryWithCardAndMemberResponseDto.class, cardHistory, cardHistory.cardId,
					member.memberId, member.name, cardHistory.cardSnapShot))
			.from(cardHistory)
			.innerJoin(member)
			.on(cardHistory.solvedMemberId.eq(member.memberId), member.isDeleted.eq(false))
			.where(cardHistory.solvedMemberId.eq(solvedMemberId), cardHistory.isDeleted.eq(false),
				lessOrEqCardHistoryId(lastCardHistoryId))
			.orderBy(cardHistory.cardHistoryId.tsid.desc())
			.limit(pageSize + 1L)
			.fetch();

		return CursorPage.<CardHistoryWithCardAndMemberResponseDto>builder()
			.lastIdExtractStrategy(cardHistoryDto -> cardHistoryDto.getCardHistory().getCardHistoryId())
			.contents(contents)
			.pageSize(pageSize)
			.sortOrder(SortOrder.DESC)
			.build();
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

	@Override
	public Map<Id, Pair<Double, Long>> findCardHistoryScoresAvgAndCountsByCardIds(List<Id> cardIds) {
		Map<Id, Pair<Double, Long>> collect = jpaQueryFactory.select(cardHistory.cardId, cardHistory.score.score.avg(),
				cardHistory.cardId.count())
			.from(cardHistory)
			.innerJoin(card)
			.on(cardHistory.cardId.eq(card.cardId),
				card.isDeleted.eq(false))
			.where(cardHistory.cardId.in(cardIds),
				cardHistory.isDeleted.eq(false))
			.groupBy(cardHistory.cardId)
			.fetch()
			.stream()
			.collect(Collectors.toMap(tuple -> tuple.get(cardHistory.cardId), tuple -> {
				Double avg = tuple.get(cardHistory.score.score.avg());
				avg = avg == null ? 0.0 : avg;
				Long count = tuple.get(cardHistory.cardId.count());
				count = count == null ? 0L : count;
				return Pair.of(avg, count);
			}));
		for (Id cardId : cardIds) {
			collect.putIfAbsent(cardId, Pair.of(0.0, 0L));
		}
		return collect;
	}

	@Override
	public Map<Id, Double> findCardScoreAvgMapByCategoryId(Id categoryId) {
		List<Tuple> tuples = jpaQueryFactory.select(card.cardId, cardHistory.score.score.avg())
			.from(cardHistory)
			.where(cardHistory.isDeleted.eq(false))
			.join(card)
			.on(cardHistory.cardId.eq(card.cardId),
				card.isDeleted.eq(false),
				card.categoryId.eq(categoryId))
			.groupBy(card.cardId)
			.limit(1000)
			.fetch();
		return tuples.stream()
			.collect(Collectors.toMap(tuple -> tuple.get(0, Id.class), tuple -> {
				Double aDouble = tuple.get(1, Double.class);
				return aDouble == null ? 0L : aDouble;
			}));
	}

	@Override
	public Optional<CardStatisticsDto> findCardHistoryScoresAvgAndCountsByCardId(Id cardId) {
		Tuple tuple = jpaQueryFactory.select(cardHistory.cardId, cardHistory.score.score.avg(),
				cardHistory.cardId.count())
			.from(cardHistory)
			.innerJoin(card)
			.on(cardHistory.cardId.eq(card.cardId),
				card.isDeleted.eq(false))
			.where(cardHistory.cardId.eq(cardId),
				cardHistory.isDeleted.eq(false))
			.groupBy(cardHistory.cardId)
			.fetchOne();

		if (tuple == null) {
			return Optional.empty();
		}
		double scoreAvg = Optional.ofNullable(tuple.get(cardHistory.score.score.avg())).orElse(0.0);
		long solveCount = Optional.ofNullable(tuple.get(cardHistory.cardId.count())).orElse(0L);
		CardStatisticsDto statisticsDto = CardStatisticsDto.builder()
			.tryCount(solveCount)
			.scoreAvg(scoreAvg)
			.build();
		return Optional.of(statisticsDto);
	}

	@Nullable
	private BooleanExpression lessOrEqCardHistoryId(@Nullable Id lastCardHistoryId) {
		return lastCardHistoryId == null ? null : cardHistory.cardHistoryId.tsid.loe(lastCardHistoryId.getTsid());
	}
}
