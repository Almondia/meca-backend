package com.almondia.meca.common.configuration.jackson.module.nlp;

import java.io.IOException;

import com.almondia.meca.cardhistory.infra.morpheme.EngNlpToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class EngNlpTokenDeserializer extends StdDeserializer<EngNlpToken> {

	protected EngNlpTokenDeserializer() {
		this(null);
	}

	protected EngNlpTokenDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public EngNlpToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode jsonNode = p.getCodec().readTree(p);
		String morph = jsonNode.get("morph").asText();
		String pos = jsonNode.get("pos").asText();
		return new EngNlpToken(morph, pos);
	}
}
