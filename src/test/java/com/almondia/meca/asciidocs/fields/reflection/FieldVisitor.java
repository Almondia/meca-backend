package com.almondia.meca.asciidocs.fields.reflection;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

public interface FieldVisitor {

	List<String> extractFieldNames(ParameterizedTypeReference<?> typeReference);
}

