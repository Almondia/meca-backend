package com.almondia.meca.auth.s3.controller.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import lombok.Getter;

@Getter
public class MultiDownloadPreSignedUrlResponseDto {

	private final List<String> urls;
	private final LocalDateTime expirationDate;

	public MultiDownloadPreSignedUrlResponseDto(List<String> urls, Date expirationDate) {
		this.urls = urls;
		this.expirationDate = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
