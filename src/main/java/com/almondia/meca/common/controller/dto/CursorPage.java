package com.almondia.meca.common.controller.dto;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

	private static final String INVALID_ENTITY_TYPE = "적절한 엔티티 타입이 아닙니다";
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

	public static <T> CursorPage<T> of(List<T> contents, int pageSize, SortOrder sortOrder) {
		Id lastId = extractLastId(contents, pageSize);
		List<T> newContents = new ArrayList<>(contents);
		if (lastId != null) {
			newContents.remove(newContents.size() - 1);
		}
		return new CursorPage<>(newContents, lastId, pageSize, sortOrder);
	}

	public static <T> CursorPage<T> empty() {
		return new CursorPage<>(Collections.emptyList(), null, 0, SortOrder.ASC);
	}

	private static <T> boolean hasLastPage(List<T> contents, int pageSize) {
		return contents.size() == pageSize + 1;
	}

	private static <T> Id extractLastId(List<T> contents, int pageSize) {
		if (!hasLastPage(contents, pageSize)) {
			return null;
		}
		Object object = contents.get(pageSize);
		Field[] declaredFields = object.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getType().equals(Id.class)) {
				field.setAccessible(true);
				try {
					return (Id)field.get(object);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(INVALID_ENTITY_TYPE);
				}
			}
		}
		throw new RuntimeException(INVALID_ENTITY_TYPE);
	}
}
