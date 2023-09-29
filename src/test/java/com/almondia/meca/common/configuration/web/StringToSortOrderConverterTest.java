package com.almondia.meca.common.configuration.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.almondia.meca.common.infra.querydsl.SortOrder;

class StringToSortOrderConverterTest {

	@Test
	void convertSortOrderAscTest() {
		StringToSortOrderConverter stringToSortOrderConverter = new StringToSortOrderConverter();
		assertEquals(SortOrder.ASC, stringToSortOrderConverter.convert("asc"));
	}

	@Test
	void convertSortOrderDescTest() {
		StringToSortOrderConverter stringToSortOrderConverter = new StringToSortOrderConverter();
		assertEquals(SortOrder.DESC, stringToSortOrderConverter.convert("desc"));
	}
}