package com.almondia.meca.common.configuration.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "aws.s3")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class S3Properties {
	private final String bucket;
	private final String accessKey;
	private final String secretKey;
	private final String region;
}
