package com.almondia.meca.common.infra.querydsl;

import java.util.Arrays;

public enum SortOrder {
	ASC("asc"),
	DESC("desc");

	private final String details;

	SortOrder(String details) {
		this.details = details;
	}

	public static SortOrder fromString(String value) {
		return Arrays.stream(SortOrder.values())
			.filter(sortOrder -> value.equalsIgnoreCase(sortOrder.details))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("asc, desc를 입력해주세요"));
	}

	@Override
	public String toString() {
		return "SortOrder{" +
			"details='" + details + '\'' +
			'}';
	}
}
