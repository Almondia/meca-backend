package com.almondia.meca.common.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 1. 직렬화/역직렬화 테스트
 * 2. 생성 순서에 따른 대소 비교 테스트
 */
@ExtendWith(SpringExtension.class)
@Import({JacksonConfiguration.class})
class IdTest {

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("id 직렬화 테스트")
	void serializeTest() throws JsonProcessingException {
		Id id = Id.generateNextId();
		String value = objectMapper.writeValueAsString(id);
		assertThat(value).isEqualTo("\"" + id + "\"");
	}

	@Test
	@DisplayName("id 역직렬화 테스트")
	void deSerializeTest() throws JsonProcessingException {
		Id id = Id.generateNextId();
		String jsonInput = "\"" + id + "\"";
		Id result = objectMapper.readValue(jsonInput, Id.class);
		assertThat(result).isEqualTo(id);
	}

	@RepeatedTest(value = 10)
	@DisplayName("id 생성시간 별 대소 비교 테스트")
	void genOrderCompareTest() {
		Id id1 = Id.generateNextId();
		Id id2 = Id.generateNextId();
		assertThat(id1).isLessThan(id2);
	}
}