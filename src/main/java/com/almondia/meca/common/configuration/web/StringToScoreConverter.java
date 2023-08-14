package com.almondia.meca.common.configuration.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.almondia.meca.cardhistory.domain.vo.Score;

public class StringToScoreConverter implements Converter<String, Score> {
	@Nullable
	@Override
	public Score convert(@NonNull String source) {
		return Score.of(source);
	}
}
