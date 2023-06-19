package com.almondia.meca.asciidocs.fields.reflection.test;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;

import com.almondia.meca.asciidocs.fields.reflection.CommonTypeCheckerManagerImpl;
import com.almondia.meca.asciidocs.fields.reflection.FieldVisitor;
import com.almondia.meca.asciidocs.fields.reflection.FieldVisitorImpl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

@Log
public class FieldVisitorTest {
	FieldVisitor fieldVisitor = new FieldVisitorImpl(new CommonTypeCheckerManagerImpl());

	@Test
	void originalClassWithObjectTest() {
		ParameterizedTypeReference<OriginPerson> typeReference = new ParameterizedTypeReference<>() {
		};
		List<String> result = fieldVisitor.extractFieldNames(typeReference);
		assertThat(result).containsAll(List.of("name", "age", "career", "address.street", "address.name", "hobbies"));
		log.info("result: " + result.toString());
	}

	@Test
	void genericTypeTest() {
		ParameterizedTypeReference<Person<Address>> typeReference = new ParameterizedTypeReference<>() {
		};
		List<String> result = fieldVisitor.extractFieldNames(typeReference);
		assertThat(result).containsAll(List.of("name", "age", "address.street", "address.name", "hobbies"));
		log.info("result: " + result.toString());
	}

	@Test
	void listCollectionsAndGenericTypeTest() {
		ParameterizedTypeReference<Page<OriginPerson>> typeReference = new ParameterizedTypeReference<>() {
		};
		List<String> result = fieldVisitor.extractFieldNames(typeReference);
		assertThat(result).containsAll(List.of("content[].name", "content[].age", "content[].address.street",
			"content[].address.name", "content[].hobbies", "pageSize", "hasNext"));
		log.info("result: " + result.toString());
	}

	@Test
	void listCollectionAndDuplicateGenericTypeTest() {
		ParameterizedTypeReference<Page<Person<Address>>> typeReference = new ParameterizedTypeReference<>() {
		};
		List<String> result = fieldVisitor.extractFieldNames(typeReference);
		assertThat(result).containsAll(List.of("content[].name", "content[].age", "content[].address.street",
			"content[].address.name", "content[].hobbies", "pageSize", "hasNext"));
		log.info("result: " + result.toString());
	}

	@Test
	void helloTest() {
		ParameterizedTypeReference<Hello> typeReference = new ParameterizedTypeReference<>() {
		};
		List<String> result = fieldVisitor.extractFieldNames(typeReference);
		assertThat(result).containsAll(List.of("name?", "hello?"));
		log.info("result: " + result.toString());
	}
}

@AllArgsConstructor
@Getter
@ToString
class Page<T> {
	private List<T> content;
	private int pageSize;
	private boolean hasNext;
}

@Getter
@AllArgsConstructor
@ToString
class Address {
	private int street;
	private int name;
}

@Getter
@AllArgsConstructor
@ToString
class Person<T> {
	private String name;
	private int age;
	private T address;
	private List<String> hobbies;
}

@Getter
@AllArgsConstructor
@ToString
class OriginPerson {
	private String name;
	private int age;
	private Address address;
	private List<Hobby> hobbies;
	private List<String> career;
}

@Getter
enum Hobby {
	SPORTS, MUSIC, MOVIE
}

@Getter
class Hello {
	@Nullable
	private List<String> name;

	@Nullable
	private String hello;
}
