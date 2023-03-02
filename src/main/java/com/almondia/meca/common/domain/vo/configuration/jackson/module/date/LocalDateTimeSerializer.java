package com.almondia.meca.common.domain.vo.configuration.jackson.module.date;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

	protected LocalDateTimeSerializer() {
		this(null);
	}

	protected LocalDateTimeSerializer(Class<LocalDateTime> t) {
		super(t);
	}

	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
		throws IOException {
		String format = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		gen.writeString(format);
	}
}