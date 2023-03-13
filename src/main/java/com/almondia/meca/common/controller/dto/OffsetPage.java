package com.almondia.meca.common.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class OffsetPage<T> {
	private List<T> contents;
	private int totalPages;
	private int totalElements;
	private int pageNumber;
	private int pageSize;

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
