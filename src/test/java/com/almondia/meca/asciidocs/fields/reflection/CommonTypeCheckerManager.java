package com.almondia.meca.asciidocs.fields.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface CommonTypeCheckerManager {

	void addChecker(CommonTypeChecker commonTypeChecker);

	void addAllCheckers(CommonTypeChecker... commonTypeCheckers);

	boolean isCommonType(Type type);

	boolean isCommonField(Field field);
}
