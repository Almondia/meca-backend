package com.almondia.meca.common.configuration.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.almondia.meca.auth.s3.domain.vo.ImageExtension;

class StringToImageExtensionConverterTest {

	@Test
	void convertImageExtensionJPEGTest() {
		StringToImageExtensionConverter converter = new StringToImageExtensionConverter();
		assertEquals(ImageExtension.JPEG, converter.convert("JPEG"));
	}

	@Test
	void convertImageExtensionJPGTest() {
		StringToImageExtensionConverter converter = new StringToImageExtensionConverter();
		assertEquals(ImageExtension.JPG, converter.convert("JPG"));
	}

	@Test
	void convertImageExtensionPNGTest() {
		StringToImageExtensionConverter converter = new StringToImageExtensionConverter();
		assertEquals(ImageExtension.PNG, converter.convert("PNG"));
	}

	@Test
	void convertImageExtensionGIFTest() {
		StringToImageExtensionConverter converter = new StringToImageExtensionConverter();
		assertEquals(ImageExtension.GIF, converter.convert("GIF"));
	}

	@Test
	void convertImageExtensionWEBPTest() {
		StringToImageExtensionConverter converter = new StringToImageExtensionConverter();
		assertEquals(ImageExtension.WEBP, converter.convert("WEBP"));
	}
}