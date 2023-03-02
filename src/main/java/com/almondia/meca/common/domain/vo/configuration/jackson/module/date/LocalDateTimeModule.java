package com.almondia.meca.common.domain.vo.configuration.jackson.module.date;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

public class LocalDateTimeModule extends SimpleModule {

	@Override
	public String getModuleName() {
		return super.getModuleName();
	}

	@Override
	public void setupModule(SetupContext context) {
		SimpleSerializers simpleSerializers = new SimpleSerializers();
		simpleSerializers.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());

		context.addSerializers(simpleSerializers);
	}
}
