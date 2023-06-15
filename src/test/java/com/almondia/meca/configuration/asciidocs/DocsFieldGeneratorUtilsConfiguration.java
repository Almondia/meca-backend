package com.almondia.meca.configuration.asciidocs;

import java.time.LocalDateTime;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.almondia.meca.asciidocs.fields.DocsFieldGeneratorUtils;
import com.almondia.meca.asciidocs.fields.reflection.CommonTypeCheckerManager;
import com.almondia.meca.asciidocs.fields.reflection.CommonTypeCheckerManagerImpl;
import com.almondia.meca.asciidocs.fields.reflection.FieldVisitor;
import com.almondia.meca.asciidocs.fields.reflection.FieldVisitorImpl;
import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

@TestConfiguration
public class DocsFieldGeneratorUtilsConfiguration {

	@Bean
	public DocsFieldGeneratorUtils docsFieldGeneratorUtils() {
		CommonTypeCheckerManager commonTypeCheckerManager = new CommonTypeCheckerManagerImpl();
		commonTypeCheckerManager.addChecker(type -> {
			if (type instanceof Class) {
				Class<?> classType = (Class<?>)type;
				return Wrapper.class.isAssignableFrom(classType);
			}
			return false;
		});
		commonTypeCheckerManager.addChecker(type -> {
			if (type instanceof Class) {
				Class<?> classType = (Class<?>)type;
				return LocalDateTime.class.isAssignableFrom(classType);
			}
			return false;
		});

		commonTypeCheckerManager.addChecker(type -> {
			if (type instanceof Class) {
				Class<?> classType = (Class<?>)type;
				return Boolean.class.isAssignableFrom(classType);
			}
			return false;
		});
		FieldVisitor fieldVisitor = new FieldVisitorImpl(commonTypeCheckerManager);
		return new DocsFieldGeneratorUtils(fieldVisitor);
	}
}
