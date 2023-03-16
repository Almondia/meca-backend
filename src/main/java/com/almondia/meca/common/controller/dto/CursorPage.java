package com.almondia.meca.common.controller.dto;

import java.util.Collections;
import java.util.List;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class CursorPage<T> {

	private final List<T> contents;
	private final Id hasNext;
	private final int pageSize;
	private final SortOrder sortOrder;

	public CursorPage(List<T> contents, Id hasNext, int pageSize, SortOrder sortOrder) {
		this.contents = Collections.unmodifiableList(contents);
		this.hasNext = hasNext;
		this.pageSize = pageSize;
		this.sortOrder = sortOrder;
	}
}
