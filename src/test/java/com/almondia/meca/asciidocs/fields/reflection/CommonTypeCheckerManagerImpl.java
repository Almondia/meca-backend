package com.almondia.meca.asciidocs.fields.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonTypeCheckerManagerImpl implements CommonTypeCheckerManager {

	private final List<CommonTypeChecker> commonTypeCheckers = new ArrayList<>();

	public CommonTypeCheckerManagerImpl() {
		commonTypeCheckers.add(type -> {
			if (type instanceof ParameterizedType) {
				return false;
			}
			if (type instanceof Class) {
				Class<?> classType = (Class<?>)type;
				return classType.isPrimitive()
					|| classType.isEnum()
					|| String.class.isAssignableFrom(classType);
			}
			return false;
		});
	}

	@Override
	public void addChecker(CommonTypeChecker commonTypeChecker) {
		commonTypeCheckers.add(commonTypeChecker);
	}

	@Override
	public void addAllCheckers(CommonTypeChecker... commonTypeCheckers) {
		this.commonTypeCheckers.addAll(Arrays.asList(commonTypeCheckers));
	}

	@Override
	public boolean isCommonType(Type type) {
		return commonTypeCheckers.stream()
			.allMatch(commonTypeChecker -> commonTypeChecker.isCommonType(type));
	}

	@Override
	public boolean isCommonField(Field field) {
		return commonTypeCheckers.stream()
			.allMatch(commonTypeChecker -> commonTypeChecker.isCommonType(field.getGenericType()));
	}
}
