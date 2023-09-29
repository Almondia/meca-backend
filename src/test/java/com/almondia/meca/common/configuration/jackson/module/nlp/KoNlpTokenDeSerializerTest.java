package com.almondia.meca.common.configuration.jackson.module.nlp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.almondia.meca.cardhistory.infra.morpheme.token.KoNlpToken;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@Import(JacksonConfiguration.class)
class KoNlpTokenDeSerializerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("KoNlpTokenDeSerializer 테스트")
	void deSerializeTest() throws Exception {
		String json = "{\"morph\":\"안녕\",\"pos\":\"NNG\",\"beginIndex\":0,\"endIndex\":2}";
		KoNlpToken token = objectMapper.readValue(json, KoNlpToken.class);
		assertEquals("안녕", token.getMorph());
		assertEquals("NNG", token.getPos());
		assertEquals(0, token.getBeginIndex());
		assertEquals(2, token.getEndIndex());
	}
}