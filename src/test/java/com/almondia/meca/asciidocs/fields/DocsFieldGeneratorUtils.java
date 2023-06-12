package com.almondia.meca.asciidocs.fields;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Attributes;

import com.querydsl.core.util.StringUtils;

public class DocsFieldGeneratorUtils {

	private static final YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
	private static final ReloadableResourceBundleMessageSource restDocsMessageSource = new ReloadableResourceBundleMessageSource();

	private static final String DESCRIPTION = "description";
	private static final String CONSTRAINTS = "constraints";

	static {
		savePropertiesFile("docs_ko.yml", "docs_ko.properties");
		savePropertiesFile("docs_en.yml", "docs_en.properties");
		restDocsMessageSource.setBasename("classpath:docs");
		restDocsMessageSource.setDefaultEncoding("UTF-8");
		restDocsMessageSource.setDefaultLocale(Locale.KOREAN);
	}

	public static ResponseFieldsSnippet generateResponseFieldSnippet(Class<?> clazz, String domainName, Locale locale) {
		List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field declaredField : declaredFields) {
			declaredField.setAccessible(true);
			String attributeName = declaredField.getName();
			if (attributeName.startsWith("is")) {
				attributeName = StringUtils.uncapitalize(attributeName.substring(2));
			}
			fieldDescriptors.add(fieldWithPath(attributeName).description(
				getFieldDescription(domainName, declaredField.getName(), locale)));
		}
		return responseFields(fieldDescriptors);
	}

	public static RequestFieldsSnippet generateRequestFieldSnippet(Class<?> clazz, String domainName, Locale locale) {
		List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field declaredField : declaredFields) {
			declaredField.setAccessible(true);
			String attributeName = declaredField.getName();
			if (attributeName.startsWith("is")) {
				attributeName = StringUtils.uncapitalize(attributeName.substring(2));
			}
			FieldDescriptor fieldDescriptor = fieldWithPath(attributeName).description(
				getFieldDescription(domainName, declaredField.getName(), locale));

			Annotation annotation = declaredField.getAnnotation(Nullable.class);
			if (annotation != null) {
				fieldDescriptor.optional();
			}
			fieldDescriptor.attributes(
				Attributes.key(CONSTRAINTS).value(getFieldConstraint(domainName, declaredField.getName(), locale)));
			fieldDescriptors.add(fieldDescriptor);
		}
		return requestFields(fieldDescriptors);
	}

	private static String getFieldDescription(String domainName, String fieldName, Locale locale) {
		return restDocsMessageSource.getMessage(domainName + "." + DESCRIPTION + "." + fieldName,
			new String[] {}, locale);
	}

	private static String getFieldConstraint(String domainName, String fieldName, Locale locale) {
		return restDocsMessageSource.getMessage(domainName + "." + CONSTRAINTS + "." + fieldName,
			new String[] {}, locale);
	}

	private static void savePropertiesFile(String ymlPath, String fileName) {
		String testResourcePath = "src/test/resources/";
		ClassPathResource classPathResource = new ClassPathResource(ymlPath);
		try {
			yamlFactory.setResources(classPathResource);
			Properties properties = yamlFactory.getObject();
			if (properties != null) {
				properties.store(new java.io.FileOutputStream(testResourcePath + fileName), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
