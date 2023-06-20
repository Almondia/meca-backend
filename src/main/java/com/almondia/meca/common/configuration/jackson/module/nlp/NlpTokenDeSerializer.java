package com.almondia.meca.common.configuration.jackson.module.nlp;

import java.io.IOException;

import com.almondia.meca.cardhistory.domain.vo.NlpToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class NlpTokenDeSerializer extends StdDeserializer<NlpToken> {

	protected NlpTokenDeSerializer() {
		this(null);
	}

	protected NlpTokenDeSerializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public NlpToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode jsonNode = p.getCodec().readTree(p);
		String morph = jsonNode.get("morph").asText();
		String pos = jsonNode.get("pos").asText();
		int startIndex = jsonNode.get("beginIndex").asInt();
		int endIndex = jsonNode.get("endIndex").asInt();
		return new NlpToken(morph, pos, startIndex, endIndex);
	}
}
