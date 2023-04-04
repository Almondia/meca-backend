package com.almondia.meca.auth.s3.controller.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.Getter;

@Getter
public class DownloadPreSignedUrlResponseDto {

	private final String url;
	private final LocalDateTime expirationDate;

	public DownloadPreSignedUrlResponseDto(String url, Date expirationDate) {
		this.url = url;
		this.expirationDate = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
