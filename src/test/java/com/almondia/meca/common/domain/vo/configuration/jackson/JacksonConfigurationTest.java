package com.almondia.meca.common.domain.vo.configuration.jackson;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 1. 직렬화/역직렬화 수행시 snake_case를 기준으로 동작해야 한다
 */
@ExtendWith(SpringExtension.class)
@Import({JacksonConfiguration.class})
class JacksonConfigurationTest {

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("직렬화시 snake case형태의 json string으로 변환해야 함")
	void shouldReturnSnakeCaseKeyNameWhenSerializeUsingObjectMapper() throws JsonProcessingException {
		Dto dto = new Dto("hello", 10);
		String value = objectMapper.writeValueAsString(dto);
		assertThat(value).contains("user_name", "user_age");
	}

	@Test
	@DisplayName("역직렬화시 snake case형태의 json string을 객체로 변환할 수 있어야 함")
	void shouldReturnClassWhenDeSerializeSnakeCaseKeyJsonStringUsingObjectMapper() throws JsonProcessingException {
		String input = "{\"user_name\":\"hello\",\"user_age\":10}";
		Dto dto = objectMapper.readValue(input, Dto.class);
		assertThat(dto)
			.hasFieldOrPropertyWithValue("userName", "hello")
			.hasFieldOrPropertyWithValue("userAge", 10);
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Setter
	static class Dto {
		private String userName;
		private int userAge;
	}
}