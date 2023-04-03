package com.almondia.meca.common.configuration.s3;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class S3PropertiesTest {

	@Autowired
	S3Properties s3Properties;

	@Test
	void test() {
		assertThat(s3Properties).isNotNull()
			.hasFieldOrProperty("accessKey")
			.hasFieldOrProperty("bucket")
			.hasFieldOrProperty("secretKey")
			.hasFieldOrProperty("region");
	}
}