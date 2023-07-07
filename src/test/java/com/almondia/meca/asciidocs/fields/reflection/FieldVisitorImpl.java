package com.almondia.meca.asciidocs.fields.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;

public class FieldVisitorImpl implements FieldVisitor {

	private static final String OPTIONAL_MARK = "?";
	private final CommonTypeCheckerManager commonTypeCheckerManager;

	public FieldVisitorImpl(CommonTypeCheckerManager commonTypeCheckerManager) {
		this.commonTypeCheckerManager = commonTypeCheckerManager;
	}

	@Override
	public List<String> extractFieldNames(ParameterizedTypeReference<?> typeReference) {
		Type referenceType = typeReference.getType();
		if (referenceType instanceof Class) {
			Class<?> type = (Class<?>)referenceType;
			return searchField("", type.getDeclaredFields(), null);
		}
		if (referenceType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)referenceType;
			Type rawType = parameterizedType.getRawType();
			Type parameterType = parameterizedType.getActualTypeArguments()[0];
			if (rawType instanceof Class) {
				Class<?> type = (Class<?>)rawType;
				return searchField("", type.getDeclaredFields(), parameterType);
			}
		}
		return null;
	}

	private List<String> searchField(String path, Field[] fields, Type parameterType) {
		List<String> result = new ArrayList<>();
		result.addAll(searchCommonField(path, fields));
		result.addAll(searchListField(path, fields, parameterType));
		result.addAll(searchObjectField(path, fields, parameterType));
		return result;
	}

	private List<String> searchCommonField(String path, Field[] fields) {
		List<String> result = new ArrayList<>();
		for (Field field : fields) {
			if (isInvalidModifiers(field)) {
				continue;
			}
			if (commonTypeCheckerManager.isCommonField(field)) {
				Annotation annotation = field.getAnnotation(Nullable.class);
				if (annotation != null) {
					result.add(makePath(path, field) + OPTIONAL_MARK);
					continue;
				}
				result.add(makePath(path, field));
			}
		}
		return result;
	}

	private List<String> searchListField(String path, Field[] fields, Type parameterType) {
		List<String> result = new ArrayList<>();
		for (Field field : fields) {
			if (isInvalidModifiers(field)) {
				continue;
			}
			Type type = field.getGenericType();
			if (type instanceof ParameterizedType && ((ParameterizedType)type).getRawType().equals(List.class)) {
				ParameterizedType parameterizedType = (ParameterizedType)type;
				Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
				if (commonTypeCheckerManager.isCommonType(actualTypeArgument)) {
					Annotation annotation = field.getAnnotation(Nullable.class);
					if (annotation != null) {
						result.add(makePath(path, field) + OPTIONAL_MARK);
						continue;
					}
					result.add(makePath(path, field));
					continue;
				}
				if (actualTypeArgument instanceof Class) {
					result.addAll(
						searchField(makePath(path, field) + "[]", ((Class<?>)actualTypeArgument).getDeclaredFields(),
							null));
					continue;
				}
				if (parameterType instanceof ParameterizedType) {
					ParameterizedType parameterizedParameterType = (ParameterizedType)parameterType;
					Type parameterRawType = parameterizedParameterType.getRawType();
					Type parameterActualTypeArgument = parameterizedParameterType.getActualTypeArguments()[0];
					result.addAll(
						searchField(makePath(path, field) + "[]", ((Class<?>)parameterRawType).getDeclaredFields(),
							parameterActualTypeArgument));
					continue;
				}
				result.addAll(
					searchField(makePath(path, field) + "[]", ((Class<?>)parameterType).getDeclaredFields(), null));
			}
		}
		return result;
	}

	private List<String> searchObjectField(String path, Field[] fields, @Nullable Type parameterType) {
		List<String> result = new ArrayList<>();
		for (Field field : fields) {
			if (isInvalidModifiers(field)) {
				continue;
			}
			if (commonTypeCheckerManager.isCommonField(field)) {
				continue;
			}
			Class<?> type = field.getType();
			if (type.equals(Object.class) && parameterType != null) {
				result.addAll(searchField(makePath(path, field), ((Class<?>)parameterType).getDeclaredFields(), null));
				continue;
			}
			result.addAll(searchField(makePath(path, field), type.getDeclaredFields(), null));
		}
		return result;
	}

	private String makePath(String path, Field field) {
		return path.isEmpty() ? field.getName() : path + "." + field.getName();
	}

	private boolean isInvalidModifiers(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isNative(modifiers) || Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers);
	}

}
