package com.almondia.meca.common.domain.vo.configuration.jackson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.almondia.meca.common.domain.vo.configuration.jackson.module.date.LocalDateTimeModule;
import com.almondia.meca.common.domain.vo.configuration.jackson.module.wrapper.WrapperModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@Configuration
public class JacksonConfiguration {

	@Bean
	@Primary
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper()
			.registerModule(new LocalDateTimeModule())
			.registerModule(new WrapperModule())
			.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
	}
}