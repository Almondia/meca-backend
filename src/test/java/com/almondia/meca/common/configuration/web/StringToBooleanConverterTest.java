package com.almondia.meca.common.configuration.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringToBooleanConverterTest {

	@Test
	void convertBooleanTrueTest() {
		StringToBooleanConverter stringToBooleanConverter = new StringToBooleanConverter();
		Boolean result = stringToBooleanConverter.convert("true");
		assertEquals(true, result);
	}

	@Test
	void convertBooleanFalseTest() {
		StringToBooleanConverter stringToBooleanConverter = new StringToBooleanConverter();
		Boolean result = stringToBooleanConverter.convert("false");
		assertEquals(false, result);
	}
}