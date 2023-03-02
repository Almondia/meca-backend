package com.almondia.meca.common.domain.vo.configuration.jackson;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.json.JSONException;
import org.json.JSONObject;
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
 * 2. 날짜 데이터도 어노테이션 사용 없이 직렬화/역직렬화가 잘 동작해야 한다
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

	@Test
	@DisplayName("직렬화시 LocalDateTime이 어노테이션 없이 잘 동작해야 함")
	void shouldSerializeWithoutJacksonAnnotation() throws JsonProcessingException, JSONException {
		DateForTest dateForTest = new DateForTest(LocalDateTime.now());
		String valueAsString = objectMapper.writeValueAsString(dateForTest);
		boolean isISO_LOCAL_DATE_TIME = checkDateTimeFormat(valueAsString);
		assertThat(isISO_LOCAL_DATE_TIME).isTrue();
	}

	@Test
	@DisplayName("역직렬화시 입력이 ISO_LOCAL_DATE_TIME인 경우 잘 동작해야 함")
	void shouldDeserializeWhenInputFormatIsISO_LOCAL_DATE_TIME() throws JsonProcessingException {
		String jsonString = "{\"date_time\":\"2023-03-02T13:24:27.5431758\"}";
		DateForTest object = objectMapper.readValue(jsonString, DateForTest.class);
		assertThat(object)
			.hasFieldOrPropertyWithValue("dateTime",
				LocalDateTime.parse("2023-03-02T13:24:27.5431758", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	}

	private boolean checkDateTimeFormat(String valueAsString) throws JSONException {
		JSONObject jsonObject = new JSONObject(valueAsString);
		String stringDate = jsonObject.getString("date_time");
		try {
			LocalDate date = LocalDate.parse(stringDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Setter
	static class DateForTest {
		private LocalDateTime dateTime;
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