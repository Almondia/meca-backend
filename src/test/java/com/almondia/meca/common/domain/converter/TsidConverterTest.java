package com.almondia.meca.common.domain.converter;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;

class TsidConverterTest {

	@Test
	@DisplayName("convertToDatabaseColumn 요청시 Tsid를 Long으로 변환한다")
	void convertToDatabaseColumnTest() {
		Tsid tsid = TsidCreator.getTsid();
		TsidConverter tsidConverter = new TsidConverter();
		Long aLong = tsidConverter.convertToDatabaseColumn(tsid);
		assertThat(aLong).isEqualTo(tsid.toLong());
	}

	@Test
	@DisplayName("convertToDatabaseColumn 요청시 Tsid가 null이면 null을 반환한다")
	void convertToDatabaseColumnWhenTsidIsNullTest() {
		TsidConverter tsidConverter = new TsidConverter();
		Long aLong = tsidConverter.convertToDatabaseColumn(null);
		assertThat(aLong).isNull();
	}

	@Test
	@DisplayName("convertToEntityAttribute 요청시 Long을 Tsid로 변환한다")
	void convertToEntityAttributeTest() {
		Tsid tsid = TsidCreator.getTsid();
		TsidConverter tsidConverter = new TsidConverter();
		Tsid entityAttribute = tsidConverter.convertToEntityAttribute(tsid.toLong());
		assertThat(entityAttribute).isEqualTo(tsid);
	}

	@Test
	@DisplayName("convertToEntityAttribute 요청시 Long이 null이면 null을 반환한다")
	void convertToEntityAttributeWhenLongIsNullTest() {
		TsidConverter tsidConverter = new TsidConverter();
		Tsid entityAttribute = tsidConverter.convertToEntityAttribute(null);
		assertThat(entityAttribute).isNull();
	}
}