package com.almondia.meca.auth.s3.controller.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.Getter;

@Getter
public class UploadPreSignedUrlResponseDto {

	private final String url;
	private final LocalDateTime expirationDate;
	private final String objectKey;

	public UploadPreSignedUrlResponseDto(String url, Date date, String objectKey) {
		this.url = url;
		this.expirationDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		this.objectKey = objectKey;
	}
}
