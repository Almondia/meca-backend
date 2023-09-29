package com.almondia.meca.common.configuration.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.almondia.meca.cardhistory.domain.vo.Score;

class StringToScoreConverterTest {

	@Test
	void convertScoreTest() {
		StringToScoreConverter stringToScoreConverter = new StringToScoreConverter();
		assertEquals(stringToScoreConverter.convert("1"), Score.of("1"));
	}

}