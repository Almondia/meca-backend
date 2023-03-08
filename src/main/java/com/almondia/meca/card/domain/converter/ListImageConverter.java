package com.almondia.meca.card.domain.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.almondia.meca.card.domain.vo.Image;

@Converter
public class ListImageConverter implements AttributeConverter<List<Image>, String> {

	private static final String SPLIT_CHAR = ",";

	@Override
	public String convertToDatabaseColumn(List<Image> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return null;
		}
		return attribute.stream().map(Image::toString).collect(Collectors.joining(SPLIT_CHAR));
	}

	@Override
	public List<Image> convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(dbData.split(SPLIT_CHAR)).map(Image::new).collect(Collectors.toList());
	}
}
