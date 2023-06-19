package com.almondia.meca.asciidocs.fields;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.util.StringUtils;

import com.almondia.meca.asciidocs.fields.reflection.FieldVisitor;

public class DocsFieldGeneratorUtils {

	private static final String DESCRIPTION = "description";
	private static final String CONSTRAINTS = "constraints";
	private static final String OPTIONAL_MARK = "?";

	private final YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
	private final ReloadableResourceBundleMessageSource restDocsMessageSource = new ReloadableResourceBundleMessageSource();
	private final FieldVisitor fieldVisitor;

	public DocsFieldGeneratorUtils(FieldVisitor fieldVisitor) {
		savePropertiesFile("docs_ko.yml", "docs_ko.properties");
		savePropertiesFile("docs_en.yml", "docs_en.properties");
		restDocsMessageSource.setBasename("classpath:docs");
		restDocsMessageSource.setDefaultEncoding("UTF-8");
		restDocsMessageSource.setDefaultLocale(Locale.KOREAN);
		this.fieldVisitor = fieldVisitor;
	}

	public ResponseFieldsSnippet generateResponseFieldSnippet(ParameterizedTypeReference<?> typeReference,
		String domainName, Locale locale) {
		List<String> pathNames = fieldVisitor.extractFieldNames(typeReference);
		List<FieldDescriptor> descriptors = pathNames.stream()
			.map(pathFieldName -> {
				String fieldName = getFieldName(pathFieldName);
				return fieldWithPath(convertFieldPathName(pathFieldName)).description(
					getFieldValue(domainName, DESCRIPTION, fieldName, locale));
			})
			.collect(Collectors.toList());
		return responseFields(descriptors);
	}

	public RequestFieldsSnippet generateRequestFieldSnippet(ParameterizedTypeReference<?> typeReference,
		String domainName, Locale locale) {
		List<String> pathNames = fieldVisitor.extractFieldNames(typeReference);
		List<FieldDescriptor> descriptors = pathNames.stream()
			.map(pathFieldName -> {
				String fieldName = getFieldName(pathFieldName);
				FieldDescriptor fieldDescriptor = fieldWithPath(convertFieldPathName(pathFieldName)).description(
						getFieldValue(domainName, DESCRIPTION, fieldName, locale))
					.attributes(
						Attributes.key(CONSTRAINTS).value(getFieldValue(domainName, CONSTRAINTS, fieldName, locale)));
				if (hasOptionalMark(fieldName)) {
					fieldDescriptor.optional();
				}
				return fieldDescriptor;
			})
			.collect(Collectors.toList());
		return requestFields(descriptors);
	}

	private String getFieldValue(String domainName, String infoType, String fieldName, Locale locale) {
		return restDocsMessageSource.getMessage(domainName + "." + infoType + "." + fieldName, new String[] {},
			locale);
	}

	private String convertFieldPathName(String pathFieldName) {
		String[] split = pathFieldName.split("\\.");
		int lastIndex = split.length - 1;
		if (split[lastIndex].startsWith("is")) {
			split[lastIndex] = StringUtils.uncapitalize(split[lastIndex].substring(2));
		}
		if (split[lastIndex].endsWith(OPTIONAL_MARK)) {
			split[lastIndex] = split[lastIndex].substring(0, split[lastIndex].length() - 1);
		}
		return String.join(".", split);
	}

	private boolean hasOptionalMark(String pathFieldName) {
		String[] split = pathFieldName.split("\\.");
		int lastIndex = split.length - 1;
		return split[lastIndex].endsWith(OPTIONAL_MARK);
	}

	private String getFieldName(String pathFieldName) {
		String[] split = pathFieldName.split("\\.");
		int lastIndex = split.length - 1;
		if (split[lastIndex].endsWith(OPTIONAL_MARK)) {
			split[lastIndex] = split[lastIndex].substring(0, split[lastIndex].length() - 1);
		}
		return split[lastIndex];
	}

	private void savePropertiesFile(String ymlPath, String fileName) {
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
