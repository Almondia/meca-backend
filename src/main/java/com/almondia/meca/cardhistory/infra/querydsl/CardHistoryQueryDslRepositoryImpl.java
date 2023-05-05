package com.almondia.meca.cardhistory.infra.querydsl;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryResponseDto;
import com.almondia.meca.cardhistory.domain.entity.QCardHistory;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CardHistoryQueryDslRepositoryImpl implements CardHistoryQueryDslRepository {

	private static final QCardHistory cardHistory = QCardHistory.cardHistory;

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public CursorPage<CardHistoryResponseDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize,
		Id lastCardHistoryId) {
		Assert.isTrue(pageSize >= 0, "pageSize must be greater than or equal to 0");
		Assert.isTrue(pageSize <= 1000, "pageSize must be less than or equal to 1000");

		List<CardHistoryResponseDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryResponseDto.class,
					cardHistory.cardHistoryId,
					cardHistory.cardId,
					cardHistory.userAnswer,
					cardHistory.score
				))
			.from(cardHistory)
			.where(
				cardHistory.cardId.eq(cardId),
				cardHistory.isDeleted.eq(false),
				lessOrEqCardHistoryId(lastCardHistoryId))
			.orderBy(cardHistory.cardHistoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();

		Id hasNext = null;
		if (contents.size() > pageSize) {
			hasNext = contents.get(pageSize).getCardHistoryId();
			contents.remove(pageSize);
		}

		return new CursorPage<>(contents, hasNext, pageSize, SortOrder.DESC);
	}

	@Override
	public CursorPage<CardHistoryResponseDto> findCardHistoriesByCategoryId(@NonNull Id categoryId, int pageSize,
		Id lastCardHistoryId) {
		Assert.isTrue(pageSize >= 0, "pageSize must be greater than or equal to 0");
		Assert.isTrue(pageSize <= 1000, "pageSize must be less than or equal to 1000");

		List<CardHistoryResponseDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryResponseDto.class,
					cardHistory.cardHistoryId,
					cardHistory.cardId,
					cardHistory.userAnswer,
					cardHistory.score
				))
			.from(cardHistory)
			.where(
				cardHistory.categoryId.eq(categoryId),
				cardHistory.isDeleted.eq(false),
				lessOrEqCardHistoryId(lastCardHistoryId))
			.orderBy(cardHistory.cardHistoryId.uuid.desc())
			.limit(pageSize + 1)
			.fetch();

		Id hasNext = null;
		if (contents.size() > pageSize) {
			hasNext = contents.get(pageSize).getCardHistoryId();
			contents.remove(pageSize);
		}
		return new CursorPage<>(contents, hasNext, pageSize, SortOrder.DESC);
	}

	private BooleanExpression lessOrEqCardHistoryId(Id lastCardHistoryId) {
		return lastCardHistoryId == null ? null : cardHistory.cardHistoryId.uuid.loe(lastCardHistoryId.getUuid());
	}
}