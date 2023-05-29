package com.almondia.meca.cardhistory.infra.querydsl;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
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
	public CursorPage<CardHistoryDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize,
		Id lastCardHistoryId) {
		Assert.isTrue(pageSize >= 0, "pageSize must be greater than or equal to 0");
		Assert.isTrue(pageSize <= 1000, "pageSize must be less than or equal to 1000");

		List<CardHistoryDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryDto.class,
					cardHistory.cardHistoryId,
					cardHistory.solvedUserId,
					member.name,
					cardHistory.userAnswer,
					cardHistory.score,
					category.categoryId,
					cardHistory.cardId,
					cardHistory.createdAt
				))
			.from(cardHistory)
			.innerJoin(card)
			.on(
				cardHistory.cardId.eq(card.cardId),
				card.isDeleted.eq(false)
			)
			.innerJoin(category)
			.on(
				card.categoryId.eq(category.categoryId),
				category.isDeleted.eq(false)
			)
			.innerJoin(member)
			.on(
				cardHistory.solvedUserId.eq(member.memberId),
				member.isDeleted.eq(false)
			)
			.where(
				cardHistory.isDeleted.eq(false),
				card.cardId.eq(cardId),
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
	public CursorPage<CardHistoryDto> findCardHistoriesByCategoryId(@NonNull Id categoryId, int pageSize,
		Id lastCardHistoryId) {
		Assert.isTrue(pageSize >= 0, "pageSize must be greater than or equal to 0");
		Assert.isTrue(pageSize <= 1000, "pageSize must be less than or equal to 1000");

		List<CardHistoryDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryDto.class,
					cardHistory.cardHistoryId,
					cardHistory.solvedUserId,
					member.name,
					cardHistory.userAnswer,
					cardHistory.score,
					category.categoryId,
					cardHistory.cardId,
					cardHistory.createdAt
				))
			.from(cardHistory)
			.innerJoin(card)
			.on(
				cardHistory.cardId.eq(card.cardId),
				card.isDeleted.eq(false))
			.innerJoin(category)
			.on(
				card.categoryId.eq(category.categoryId),
				category.isDeleted.eq(false)
			)
			.innerJoin(member)
			.on(
				cardHistory.solvedUserId.eq(member.memberId),
				member.isDeleted.eq(false)
			)
			.where(
				category.categoryId.eq(categoryId),
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
	public CursorPage<CardHistoryDto> findCardHistoriesBySolvedMemberId(@NonNull Id solvedMemberId,
		int pageSize, Id lastCardHistoryId) {
		Assert.isTrue(pageSize >= 0, "pageSize must be greater than or equal to 0");
		Assert.isTrue(pageSize <= 1000, "pageSize must be less than or equal to 1000");

		List<CardHistoryDto> contents = jpaQueryFactory.select(
				Projections.constructor(CardHistoryDto.class,
					cardHistory.cardHistoryId,
					cardHistory.solvedUserId,
					member.name,
					cardHistory.userAnswer,
					cardHistory.score,
					category.categoryId,
					cardHistory.cardId,
					cardHistory.createdAt
				))
			.from(cardHistory)
			.innerJoin(card)
			.on(
				cardHistory.cardId.eq(card.cardId),
				card.isDeleted.eq(false))
			.innerJoin(category)
			.on(
				card.categoryId.eq(category.categoryId),
				category.isDeleted.eq(false)
			)
			.innerJoin(member)
			.on(
				cardHistory.solvedUserId.eq(member.memberId),
				member.isDeleted.eq(false)
			)
			.where(
				cardHistory.solvedUserId.eq(solvedMemberId),
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
