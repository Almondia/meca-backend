package com.almondia.meca.common.infra.s3;

import java.net.URL;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.almondia.meca.common.configuration.s3.S3Properties;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3PreSignedUrlRequest {

	private final AmazonS3Client s3Client;
	private final S3Properties s3Properties;

	public URL requestPutPreSignedUrl(String objectKey, Date expiration) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(s3Properties.getBucket(), objectKey)
				.withMethod(HttpMethod.PUT)
				.withExpiration(expiration);
		return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
	}
}
