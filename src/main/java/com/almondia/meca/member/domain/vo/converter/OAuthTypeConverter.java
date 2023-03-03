package com.almondia.meca.member.domain.vo.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.almondia.meca.member.domain.vo.OAuthType;

@Converter(autoApply = true)
public class OAuthTypeConverter implements AttributeConverter<OAuthType, String> {

	@Override
	public String convertToDatabaseColumn(OAuthType attribute) {
		return attribute == null ? null : attribute.getDetails();
	}

	@Override
	public OAuthType convertToEntityAttribute(String dbData) {
		return dbData == null ? null : OAuthType.fromOAuthType(dbData);
	}
}
