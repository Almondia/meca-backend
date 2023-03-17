package com.almondia.meca.card.infra.querydsl;

import java.time.LocalDateTime;

import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.common.domain.vo.Id;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardSearchCriteria {

	private static final QCard card = QCard.card;

	private String startsWithTitle;
	private LocalDateTime startCreatedAt;
	private LocalDateTime endCreatedAt;
	private LocalDateTime startModifiedAt;
	private LocalDateTime endModifiedAt;
	private Id eqMemberId;
	private Id eqCategoryId;
	private boolean eqDeleted;
	private Id eqCardId;
	private Id gtCardId;
	private Id ltCardId;

	public BooleanExpression getPredicate() {
		BooleanExpression predicate = card.isNotNull();

		if (StringUtils.isNotBlank(startsWithTitle)) {
			predicate = predicate.and(card.title.title.startsWith(startsWithTitle));
		}
		if (startCreatedAt != null) {
			predicate = predicate.and(card.createdAt.goe(startCreatedAt));
		}
		if (endCreatedAt != null) {
			predicate = predicate.and(card.createdAt.loe(endCreatedAt));
		}
		if (startModifiedAt != null) {
			predicate = predicate.and(card.modifiedAt.goe(startModifiedAt));
		}
		if (endModifiedAt != null) {
			predicate = predicate.and(card.modifiedAt.loe(endModifiedAt));
		}
		if (eqMemberId != null) {
			predicate = predicate.and(card.memberId.eq(eqMemberId));
		}
		if (eqCategoryId != null) {
			predicate = predicate.and(card.categoryId.eq(eqCategoryId));
		}
		predicate = predicate.and(card.isDeleted.eq(eqDeleted));
		if (eqCardId != null) {
			predicate = predicate.and(card.cardId.eq(eqCardId));
		}
		if (gtCardId != null) {
			predicate = predicate.and(card.cardId.uuid.gt(gtCardId.getUuid()));
		}
		if (ltCardId != null) {
			predicate = predicate.and(card.cardId.uuid.lt(ltCardId.getUuid()));
		}
		return predicate;
	}
}
