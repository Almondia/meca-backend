package com.almondia.meca.common.configuration.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class S3Configuration {

	private final S3Properties s3Properties;

	@Bean
	public AmazonS3Client amazonClient() {
		return (AmazonS3Client)AmazonS3ClientBuilder.standard()
			.withRegion(s3Properties.getRegion())
			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
				s3Properties.getAccessKey(),
				s3Properties.getSecretKey())))
			.build();
	}
}
