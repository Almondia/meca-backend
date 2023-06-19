package com.almondia.meca.asciidocs.fields.reflection;

import java.lang.reflect.Type;

@FunctionalInterface
public interface CommonTypeChecker {

	boolean isCommonType(Type type);
}
