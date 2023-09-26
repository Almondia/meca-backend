package com.almondia.meca.common.configuration.jackson.module.wrapper;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.almondia.meca.common.configuration.jackson.JacksonConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;

@ExtendWith(SpringExtension.class)
@Import({JacksonConfiguration.class})
class WrapperSerializerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Wrapper 클래스 문자열 직렬화 테스트")
	void wrapperClassStringSerializationTest() throws JsonProcessingException {
		// given
		StringWrapper stringWrapper = new StringWrapper("test");

		// when
		String s = objectMapper.writeValueAsString(stringWrapper);

		// then
		assertThat(s).isEqualTo("\"test\"");
	}

	@Test
	@DisplayName("Wrapper 클래스 UUID 직렬화 테스트")
	void wrapperClassUuidSerializationTest() throws JsonProcessingException {
		// given
		UUID uuid = UUID.randomUUID();
		UuidWrapper uuidWrapper = new UuidWrapper(uuid);

		// when
		String s = objectMapper.writeValueAsString(uuidWrapper);

		// then
		assertThat(s).isEqualTo("\"" + uuid + "\"");
	}

	@Test
	@DisplayName("Wrapper 클래스 Tsid 직렬화 테스트")
	void wrapperClassTsidSerializationTest() throws JsonProcessingException {
		// given
		Tsid tsid = TsidCreator.getTsid();
		TsidWrapper tsidWrapper = new TsidWrapper(tsid);

		// when
		String s = objectMapper.writeValueAsString(tsidWrapper);

		// then
		assertThat(s).isEqualToIgnoringCase("\"" + tsid + "\"");
	}

	@Test
	@DisplayName("Wrapper 클래스 Double 직렬화 테스트")
	void wrapperClassDoubleSerializationTest() throws JsonProcessingException {
		// given
		DoubleWrapper doubleWrapper = new DoubleWrapper(1.0);

		// when
		String s = objectMapper.writeValueAsString(doubleWrapper);

		// then
		assertThat(s).isEqualTo("1.0");
	}

	@Test
	@DisplayName("Wrapper 클래스 Integer 직렬화 테스트")
	void wrapperClassIntegerSerializationTest() throws JsonProcessingException {
		// given
		IntegerWrapper integerWrapper = new IntegerWrapper(1);

		// when
		String s = objectMapper.writeValueAsString(integerWrapper);

		// then
		assertThat(s).isEqualTo("1");
	}

	@Test
	@DisplayName("Wrapper 클래스 Float 직렬화 테스트")
	void wrapperClassFloatSerializationTest() throws JsonProcessingException {
		// given
		FloatWrapper floatWrapper = new FloatWrapper(1.0f);

		// when
		String s = objectMapper.writeValueAsString(floatWrapper);

		// then
		assertThat(s).isEqualTo("1.0");
	}

	@Test
	@DisplayName("Wrapper 클래스 Short 직렬화 테스트")
	void wrapperClassShortSerializationTest() throws JsonProcessingException {
		// given
		ShortWrapper shortWrapper = new ShortWrapper((short)1);

		// when
		String s = objectMapper.writeValueAsString(shortWrapper);

		// then
		assertThat(s).isEqualTo("1");
	}

	@Test
	@DisplayName("Wrapper 클래스 Long 직렬화 테스트")
	void wrapperClassLongSerializationTest() throws JsonProcessingException {
		// given
		LongWrapper longWrapper = new LongWrapper(1L);

		// when
		String s = objectMapper.writeValueAsString(longWrapper);

		// then
		assertThat(s).isEqualTo("1");
	}

	static class StringWrapper implements Wrapper {
		private final String value;

		public StringWrapper(String value) {
			this.value = value;
		}
	}

	static class UuidWrapper implements Wrapper {
		private final UUID value;

		public UuidWrapper(UUID value) {
			this.value = value;
		}
	}

	static class TsidWrapper implements Wrapper {
		private final Tsid value;

		public TsidWrapper(Tsid value) {
			this.value = value;
		}
	}

	static class DoubleWrapper implements Wrapper {
		private final Double value;

		public DoubleWrapper(Double value) {
			this.value = value;
		}
	}

	static class IntegerWrapper implements Wrapper {
		private final Integer value;

		public IntegerWrapper(Integer value) {
			this.value = value;
		}
	}

	static class LongWrapper implements Wrapper {
		private final Long value;

		public LongWrapper(Long value) {
			this.value = value;
		}
	}

	static class FloatWrapper implements Wrapper {
		private final Float value;

		public FloatWrapper(Float value) {
			this.value = value;
		}
	}

	static class ShortWrapper implements Wrapper {
		private final Short value;

		public ShortWrapper(Short value) {
			this.value = value;
		}
	}

}