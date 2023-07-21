package com.almondia.meca.common.controller.dto;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.lang.Nullable;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CursorPage<T> {

	private static final String INVALID_ENTITY_TYPE = "적절한 엔티티 타입이 아닙니다";
	private final List<T> contents;
	private final Id hasNext;
	private final int pageSize;
	private final SortOrder sortOrder;

	@Builder
	public CursorPage(
		@Nullable Function<T, Id> lastIdExtractStrategy,
		List<T> contents,
		int pageSize,
		SortOrder sortOrder) {
		this.hasNext = extractLastId(lastIdExtractStrategy, contents, pageSize);
		if (hasNext != null) {
			contents = contents.subList(0, contents.size() - 1);
		} else {
			contents = contents.subList(0, contents.size());
		}
		this.contents = contents;
		this.pageSize = pageSize;
		this.sortOrder = sortOrder;
	}

	protected CursorPage(List<T> contents, int pageSize, Id hasNext, SortOrder sortOrder) {
		this.contents = contents;
		this.pageSize = pageSize;
		this.hasNext = hasNext;
		this.sortOrder = sortOrder;
	}

	public static <T> CursorPage<T> empty(SortOrder sortOrder) {
		return new CursorPage<>(null, Collections.emptyList(), 0, sortOrder);
	}

	private static <T> boolean hasLastPage(List<T> contents, int pageSize) {
		return contents.size() == pageSize + 1;
	}

	@Nullable
	private Id extractLastId(@Nullable Function<T, Id> lastIdExtractStrategy, List<T> contents, int pageSize) {
		if (lastIdExtractStrategy == null) {
			return null;
		}
		if (!hasLastPage(contents, pageSize)) {
			return null;
		}
		return lastIdExtractStrategy.apply(contents.get(contents.size() - 1));
	}
}
