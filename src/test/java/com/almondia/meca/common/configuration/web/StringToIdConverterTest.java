package com.almondia.meca.common.configuration.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.almondia.meca.common.domain.vo.Id;

class StringToIdConverterTest {

	@Test
	void convertIdTest() {
		Id id = Id.generateNextId();
		StringToIdConverter stringToIdConverter = new StringToIdConverter();
		Id result = stringToIdConverter.convert(id.toString());
		assertEquals(id, result);
	}
}