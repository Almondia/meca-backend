package com.almondia.meca.common.configuration.web;

import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToBooleanConverter());
		registry.addConverter(new StringToSortOrderConverter());
		registry.addConverter(new StringToCategorySortFieldConverter());
		registry.addConverter(new StringToLocalDateTimeConverter());
	}
}
