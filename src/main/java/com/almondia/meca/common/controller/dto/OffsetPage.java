package com.almondia.meca.common.controller.dto;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OffsetPage<T> {
	private final List<T> contents;
	private final int totalPages;
	private final int totalElements;
	private final int pageNumber;
	private final int pageSize;

	public OffsetPage(List<T> contents, int totalPages, int totalElements, int pageNumber, int pageSize) {
		this.contents = Collections.unmodifiableList(contents);
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public static <T> OffsetPage<T> of(List<T> contents, int offset, int pageSize, int totalCount) {
		int pageNumber = offset / pageSize;
		int totalPages = calculateTotalPage(totalCount, pageSize);
		return new OffsetPage<>(contents, totalPages, totalCount, pageNumber, pageSize);
	}

	private static int calculateTotalPage(int totalCount, int pageSize) {
		int divided = totalCount / pageSize;
		if (totalCount > divided * pageSize) {
			return divided;
		}
		return divided - 1;
	}
}
