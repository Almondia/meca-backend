package com.almondia.meca.auth.s3.controller;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.auth.s3.controller.dto.PreSignedUrlResponseDto;
import com.almondia.meca.auth.s3.domain.vo.ImageExtension;
import com.almondia.meca.auth.s3.domain.vo.Purpose;
import com.almondia.meca.common.infra.s3.S3PreSignedUrlRequest;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/presign/")
public class PreSignedController {

	private final S3PreSignedUrlRequest s3PreSignedUrlRequest;

	@GetMapping("/images/upload")
	@Secured("ROLE_USER")
	public ResponseEntity<PreSignedUrlResponseDto> getPostPreSignedUrl(
		@AuthenticationPrincipal Member member,
		@RequestParam(name = "purpose") Purpose purpose,
		@RequestParam(name = "extension") ImageExtension extension
	) {
		String objectKey = makeObjectKey(member, purpose, extension);
		Date expirationDate = Date.from(Instant.now().plusSeconds(300L));
		String url = s3PreSignedUrlRequest.requestPutPreSignedUrl(objectKey, expirationDate).toString();
		return ResponseEntity.ok(new PreSignedUrlResponseDto(url, expirationDate, objectKey));
	}

	private String makeObjectKey(Member member, Purpose purpose, ImageExtension extension) {
		return member.getMemberId().toString() + "/" + purpose.getDetails() + "/" + UUID.randomUUID() + "."
			+ extension.getExtension();
	}
}
