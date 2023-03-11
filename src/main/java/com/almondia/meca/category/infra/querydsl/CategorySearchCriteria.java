package com.almondia.meca.category.infra.querydsl;

import java.time.LocalDateTime;

import com.almondia.meca.category.domain.entity.QCategory;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.ToString;

@Setter
@Builder
@AllArgsConstructor
@ToString
public class CategorySearchCriteria {

	private static final QCategory category = QCategory.category;

	private String startsWithTitle;
	private LocalDateTime startCreatedAt;
	private LocalDateTime endCreatedAt;
	private LocalDateTime startModifiedAt;
	private LocalDateTime endModifiedAt;
	private boolean eqShared;
	private boolean eqDeleted;

	public BooleanExpression getPredicate() {
		BooleanExpression predicate = category.isNotNull();

		if (StringUtils.isNotBlank(startsWithTitle)) {
			predicate = predicate.and(category.title.title.startsWith(startsWithTitle));
		}
		if (startCreatedAt != null) {
			predicate = predicate.and(category.createdAt.goe(startCreatedAt));
		}
		if (endCreatedAt != null) {
			predicate = predicate.and(category.createdAt.loe(endCreatedAt));
		}
		if (startModifiedAt != null) {
			predicate = predicate.and(category.modifiedAt.goe(startModifiedAt));
		}
		if (endModifiedAt != null) {
			predicate = predicate.and(category.modifiedAt.loe(endModifiedAt));
		}
		return predicate.and(category.isShared.eq(eqShared))
			.and(category.isDeleted.eq(eqDeleted));
	}
}
