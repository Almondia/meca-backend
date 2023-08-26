package com.almondia.meca.card.infra.querydsl;

import java.util.Arrays;

import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.common.infra.querydsl.SortField;
import com.querydsl.core.types.dsl.ComparableExpression;

public enum CardSortField implements SortField {
	CARD_ID("cardId", QCard.card.cardId.tsid),
	TITLE("title", QCard.card.title.title),
	CREATED_AT("createdAt", QCard.card.createdAt),
	MODIFIED_AT("modifiedAt", QCard.card.modifiedAt);

	private final String field;
	private final ComparableExpression<?> expression;

	CardSortField(String field, ComparableExpression<?> expression) {
		this.field = field;
		this.expression = expression;
	}

	public static CardSortField fromField(String field) {
		return Arrays.stream(values())
			.filter(sortField -> sortField.field.equals(field))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정렬 필드 " + field));
	}

	public ComparableExpression<?> getExpression() {
		return expression;
	}
}
