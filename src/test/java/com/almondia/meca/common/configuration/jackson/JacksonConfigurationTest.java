package com.almondia.meca.common.configuration.jackson;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 1. 직렬화/역직렬화 수행시 snake_case를 기준으로 동작해야 한다
 * 2. 날짜 데이터도 어노테이션 사용 없이 직렬화/역직렬화가 잘 동작해야 한다
 * 3. 원시 래핑 클래스의 래핑은 직렬화시 벗겨서 사용해야 한다.
 * 4. 원시 객체가 역직렬화시 원시 객체 래핑이 되어야 한다.
 * 5. 원시 래핑 클래스 인스턴스는 static, transient, native, volatile등이 modifier로 붙어 있으면 인식하지 않는다.
 * 6. 원시 래핑 클래스 인스턴스는 하나만 존재해야 하며 그 이상 존재하는 경우 오류를 출력한다.
 * 7. 인식할 수 있는 원시 객체 래핑 클래스 내부 변수 타입은 원시 타입, String, UUID 이다.
 */
@ExtendWith(SpringExtension.class)
@Import({JacksonConfiguration.class})
class JacksonConfigurationTest {

	static final Pattern INTEGER = Pattern.compile("^[^0]\\d*$");
	static final Pattern DOUBLE = Pattern.compile("^[^0]\\d*\\.\\d*[^0]$");
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
		assertThat(dto).hasFieldOrPropertyWithValue("userName", "hello").hasFieldOrPropertyWithValue("userAge", 10);
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
		assertThat(object).hasFieldOrPropertyWithValue("dateTime",
			LocalDateTime.parse("2023-03-02T13:24:27.5431758", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	}

	@Test
	@DisplayName("직렬화시 문자열 원시 객체 래핑 클래스의 경우 innerValue만 리턴")
	void shouldSerializeInnerStringValueFromSerializePrimitiveWrapperClass() throws JsonProcessingException {
		Name name = new Name("hello");
		String s = objectMapper.writeValueAsString(name);
		assertThat(s).contains("\"");
	}

	@Test
	@DisplayName("역직렬화시 String 타입이 String 원시 래핑 객체로 생성되어야 한다")
	void shouldDeserializeStringToStringWrapperClass() throws JsonProcessingException {
		String jsonInput = "\"hello\"";
		Name name = objectMapper.readValue(jsonInput, Name.class);
		assertThat(name).hasFieldOrPropertyWithValue("name", "hello");
	}

	@Test
	@DisplayName("직렬화시 정수형 객체 래핑 클래스의 경우 innerValue만 리턴")
	void shouldSerializeInnerIntegerValueFromSerializePrimitiveWrapperClass() throws JsonProcessingException {
		Age age = new Age(10);
		String s = objectMapper.writeValueAsString(age);
		Matcher matcher = INTEGER.matcher(s);
		assertThat(matcher.matches()).isTrue();
	}

	@Test
	@DisplayName("역직렬화시 정수 타입이 정수 원시 래핑 객체로 생성되어야 한다")
	void shouldDeserializeIntegerToIntegerWrapperClass() throws JsonProcessingException {
		String jsonInput = "10";
		Age age = objectMapper.readValue(jsonInput, Age.class);
		assertThat(age).hasFieldOrPropertyWithValue("age", 10);
	}

	@Test
	@DisplayName("직렬화시 실수형 객체 래핑 클래스의 경우 innerValue만 리턴")
	void shouldSerializeInnerDoubleValueFromSerializePrimitiveWrapperClass() throws JsonProcessingException {
		Meter meter = new Meter(13.76);
		String s = objectMapper.writeValueAsString(meter);
		Matcher matcher = DOUBLE.matcher(s);
		assertThat(matcher.matches()).isTrue();
	}

	@Test
	@DisplayName("역직렬화시 실수 타입이 원시 래핑 객체로 생성되어야 한다")
	void shouldDeserializeDoubleToDoubleWrapperClass() throws JsonProcessingException {
		String jsonInput = "10.37";
		Meter meter = objectMapper.readValue(jsonInput, Meter.class);
		assertThat(meter).hasFieldOrPropertyWithValue("meter", 10.37);
	}

	@Test
	@DisplayName("직렬화시 UUID 타입 래핑 객체가 문자열로 직렬화 되야 함")
	void shouldSerializeUuidToStringType() throws JsonProcessingException {
		UUIDWrapper uuidWrapper = new UUIDWrapper(UUID.randomUUID());
		String value = objectMapper.writeValueAsString(uuidWrapper);
		assertThat(value).isEqualTo("\"" + uuidWrapper.getUuid() + "\"");
	}

	@Test
	@DisplayName("역직렬화시 문자열이 UUID 타입 래핑 객체가 되어야 함")
	void shouldDeSerializeStringToUuidWrapperClass() throws JsonProcessingException {
		UUID uuid = UUID.randomUUID();
		String jsonInput = "\"" + uuid + "\"";
		UUIDWrapper wrapper = objectMapper.readValue(jsonInput, UUIDWrapper.class);
		assertThat(wrapper.getUuid()).isEqualTo(uuid);
	}

	@Test
	@DisplayName("wrapper 멤버 변수가 한개가 아닌 경우 예외가 발생한다")
	void shouldThrowStateExceptionWhenInstanceVariableNumbersAreNotOne() {
		InvalidWrapperClass invalidWrapperClass = new InvalidWrapperClass("hello", 13);
		assertThatThrownBy(() -> objectMapper.writeValueAsString(invalidWrapperClass)).isInstanceOf(
			JsonMappingException.class);
	}

	@Test
	@DisplayName("static, volatile, transient, native로 이루어진 변수는 직렬화 대상에서 제외")
	void shouldNotRecognizeWhenVariableModifiersAreStaticOrVolatileOrTransientOrNative() {
		OnlyNotRecognizedModifierClass onlyNotRecognizedModifierClass = new OnlyNotRecognizedModifierClass();
		assertThatThrownBy(() -> objectMapper.writeValueAsString(onlyNotRecognizedModifierClass)).isInstanceOf(
			JsonMappingException.class);
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

	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	static class OnlyNotRecognizedModifierClass implements Wrapper {
		private static final int h = 123;
		transient int s;
		volatile String x;
	}

	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	static class InvalidWrapperClass implements Wrapper {
		private String name;
		private int age;
	}

	@NoArgsConstructor
	@EqualsAndHashCode
	@Getter
	static class UUIDWrapper implements Wrapper {
		private UUID uuid;

		public UUIDWrapper(UUID uuid) {
			this.uuid = uuid;
		}

		public UUIDWrapper(String uuid) {
			this.uuid = UUID.fromString(uuid);
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

	@EqualsAndHashCode
	static class Age implements Wrapper {
		private final int age;

		public Age(int age) {
			this.age = age;
		}
	}

	@EqualsAndHashCode
	static class Meter implements Wrapper {
		private final double meter;

		public Meter(double meter) {
			this.meter = meter;
		}
	}

	@EqualsAndHashCode
	static class Name implements Wrapper {
		private final String name;

		public Name(String name) {
			this.name = name;
		}
	}
}