package com.almondia.meca.common.configuration.jackson.module.wrapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class WrapperSerializer extends StdSerializer<Wrapper> {

	protected WrapperSerializer() {
		this(null);
	}

	protected WrapperSerializer(Class<Wrapper> t) {
		super(t);
	}

	@Override
	public void serialize(Wrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		Class<?> clazz = value.getClass();
		Field[] fields = Stream.of(clazz.getDeclaredFields()).filter(this::isPurePropertyField).toArray(Field[]::new);
		if (fields.length != 1) {
			throw new IllegalStateException("It has not one property" + "property numbers: " + fields.length);
		}
		Field field = fields[0];
		field.setAccessible(true);
		makeJsonByFieldType(value, gen, field);
	}

	private boolean isPurePropertyField(Field field) {
		int modifiers = field.getModifiers();
		return !Modifier.isTransient(modifiers) && !Modifier.isNative(modifiers) && !Modifier.isVolatile(modifiers)
			&& !Modifier.isStatic(modifiers);
	}

	private void makeJsonByFieldType(Wrapper value, JsonGenerator gen, Field field) throws IOException {
		try {
			Object o = field.get(value);
			if (o instanceof String) {
				gen.writeString((String)o);
			}
			if (o instanceof UUID) {
				gen.writeString(o.toString());
			}
			if (o instanceof Double) {
				gen.writeNumber((double)o);
			}
			if (o instanceof Integer) {
				gen.writeNumber((int)o);
			}
			if (o instanceof Float) {
				gen.writeNumber((float)o);
			}
			if (o instanceof Short) {
				gen.writeNumber((short)o);
			}
			if (o instanceof Long) {
				gen.writeNumber((long)o);
			}
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
