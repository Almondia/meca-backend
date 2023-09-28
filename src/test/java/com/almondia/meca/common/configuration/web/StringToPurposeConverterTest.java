package com.almondia.meca.common.configuration.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.almondia.meca.auth.s3.domain.vo.Purpose;

class StringToPurposeConverterTest {

	@Test
	void convertPurposeThumbnailTest() {
		StringToPurposeConverter converter = new StringToPurposeConverter();
		assertEquals(Purpose.THUMBNAIL, converter.convert("thumbnail"));
	}

	@Test
	void convertPurposeProfileTest() {
		StringToPurposeConverter converter = new StringToPurposeConverter();
		assertEquals(Purpose.PROFILE, converter.convert("profile"));
	}

	@Test
	void convertPurposeCardImageTest() {
		StringToPurposeConverter converter = new StringToPurposeConverter();
		assertEquals(Purpose.CARD_IMAGE, converter.convert("card"));
	}
}