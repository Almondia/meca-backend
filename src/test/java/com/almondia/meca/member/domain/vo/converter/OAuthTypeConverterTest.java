package com.almondia.meca.member.domain.vo.converter;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.AttributeConverter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.member.domain.vo.OAuthType;

class OAuthTypeConverterTest {

	@Test
	@DisplayName("OAuthType에서 String으로 변환")
	void shouldChangeStringTypeFromOAuthTypeTest() {
		// given
		OAuthType oAuthType = OAuthType.GOOGLE;
		AttributeConverter<OAuthType, String> converter = new OAuthTypeConverter();

		// when
		String s = converter.convertToDatabaseColumn(oAuthType);

		// then
		assertThat(s).isEqualTo("google");
	}

	@Test
	@DisplayName("String에서 OAuthType으로 변환")
	void shouldChangeOAuthTypeFromStringTest() {
		// given
		String s = "google";
		AttributeConverter<OAuthType, String> converter = new OAuthTypeConverter();

		// when
		OAuthType oAuthType = converter.convertToEntityAttribute(s);

		// then
		assertThat(oAuthType).isEqualTo(OAuthType.GOOGLE);
	}

	@Test
	@DisplayName("OAuthType에서 String으로 변환 - null")
	void shouldChangeStringTypeFromOAuthTypeNullTest() {
		// given
		OAuthType oAuthType = null;
		AttributeConverter<OAuthType, String> converter = new OAuthTypeConverter();

		// when
		String s = converter.convertToDatabaseColumn(oAuthType);

		// then
		assertThat(s).isNull();
	}

	@Test
	@DisplayName("String에서 OAuthType으로 변환 - null")
	void shouldChangeOAuthTypeFromStringNullTest() {
		// given
		String s = null;
		AttributeConverter<OAuthType, String> converter = new OAuthTypeConverter();

		// when
		OAuthType oAuthType = converter.convertToEntityAttribute(s);

		// then
		assertThat(oAuthType).isNull();
	}
}