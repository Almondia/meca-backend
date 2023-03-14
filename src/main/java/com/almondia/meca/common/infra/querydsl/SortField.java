package com.almondia.meca.common.infra.querydsl;

import com.querydsl.core.types.dsl.ComparableExpression;

/**
 * OrderSpecifier의 SortOrder를 지정하기 위해 반환할 필드의 속성 정보 반환
 */
public interface SortField {

	ComparableExpression<?> getExpression();
}
