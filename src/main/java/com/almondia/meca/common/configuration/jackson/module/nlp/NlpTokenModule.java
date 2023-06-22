package com.almondia.meca.common.configuration.jackson.module.nlp;

import com.almondia.meca.cardhistory.infra.morpheme.EngNlpToken;
import com.almondia.meca.cardhistory.infra.morpheme.NlpToken;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class NlpTokenModule extends SimpleModule {

	@Override
	public String getModuleName() {
		return super.getModuleName();
	}

	@Override
	public void setupModule(SetupContext context) {
		SimpleDeserializers simpleDeserializers = new SimpleDeserializers();
		simpleDeserializers.addDeserializer(NlpToken.class, new NlpTokenDeSerializer());
		simpleDeserializers.addDeserializer(EngNlpToken.class, new EngNlpTokenDeserializer());
		context.addDeserializers(simpleDeserializers);
	}
}
