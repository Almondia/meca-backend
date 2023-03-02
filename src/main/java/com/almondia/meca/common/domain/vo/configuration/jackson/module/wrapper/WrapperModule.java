package com.almondia.meca.common.domain.vo.configuration.jackson.module.wrapper;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

public class WrapperModule extends SimpleModule {

	@Override
	public String getModuleName() {
		return super.getModuleName();
	}

	@Override
	public void setupModule(SetupContext context) {
		SimpleSerializers simpleSerializers = new SimpleSerializers();
		simpleSerializers.addSerializer(Wrapper.class, new WrapperSerializer());

		context.addSerializers(simpleSerializers);
	}
}
