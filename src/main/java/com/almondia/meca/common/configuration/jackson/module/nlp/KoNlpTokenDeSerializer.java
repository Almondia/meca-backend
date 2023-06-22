package com.almondia.meca.common.configuration.jackson.module.nlp;

import java.io.IOException;

import com.almondia.meca.cardhistory.infra.morpheme.KoNlpToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class KoNlpTokenDeSerializer extends StdDeserializer<KoNlpToken> {

	protected KoNlpTokenDeSerializer() {
		this(null);
	}

	protected KoNlpTokenDeSerializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public KoNlpToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode jsonNode = p.getCodec().readTree(p);
		String morph = jsonNode.get("morph").asText();
		String pos = jsonNode.get("pos").asText();
		int startIndex = jsonNode.get("beginIndex").asInt();
		int endIndex = jsonNode.get("endIndex").asInt();
		return new KoNlpToken(morph, pos, startIndex, endIndex);
	}
}
