package com.almondia.meca.common.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.f4b6a3.tsid.Tsid;

@Converter(autoApply = true)
public class TsidConverter implements AttributeConverter<Tsid, Long> {
	@Override
	public Long convertToDatabaseColumn(Tsid attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.toLong();
	}

	@Override
	public Tsid convertToEntityAttribute(Long dbData) {
		return dbData == null ? null : Tsid.from(dbData);
	}
}
