package com.almondia.meca.common.configuration.jackson.module.nlp;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.almondia.meca.cardhistory.infra.morpheme.token.EngNlpToken;
import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@Import({JacksonConfiguration.class})
class EngNlpTokenDeserializerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("EngNlpTokenDeserializerTest")
	void deSerializeTest() throws JsonProcessingException {
		String engNlpTokenString = "{\"morph\":\"I\",\"pos\":\"PRP\"}";
		EngNlpToken engNlpToken = objectMapper.readValue(engNlpTokenString, EngNlpToken.class);
		assertThat(engNlpToken.getMorph()).isEqualTo("I");
		assertThat(engNlpToken.getPos()).isEqualTo("PRP");
	}

}