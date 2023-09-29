package com.almondia.meca.common.configuration.web;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class StringToLocalDateTimeConverterTest {

	@Test
	void convertDateTest() {
		StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();
		assertEquals(converter.convert("2020-01-01T00:00:00"), LocalDateTime.parse("2020-01-01T00:00:00"));
	}
}