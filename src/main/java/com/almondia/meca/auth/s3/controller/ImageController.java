package com.almondia.meca.auth.s3.controller;

import java.time.Instant;
import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.auth.s3.controller.dto.UploadPreSignedUrlResponseDto;
import com.almondia.meca.auth.s3.domain.vo.ImageExtension;
import com.almondia.meca.auth.s3.domain.vo.Purpose;
import com.almondia.meca.common.infra.s3.S3PreSignedUrlRequest;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/images/")
public class ImageController {

	private final S3PreSignedUrlRequest s3PreSignedUrlRequest;

	@GetMapping("/presign")
	@Secured("ROLE_USER")
	public ResponseEntity<UploadPreSignedUrlResponseDto> getUploadPreSignedUrl(
		@AuthenticationPrincipal Member member,
		@RequestParam(name = "purpose") Purpose purpose,
		@RequestParam(name = "extension") ImageExtension extension,
		@RequestParam(name = "fileName") String fileName
	) {
		String objectKey = makeObjectKey(member, purpose, extension, fileName);
		Date expirationDate = Date.from(Instant.now().plusSeconds(300L));
		String url = s3PreSignedUrlRequest.requestPutPreSignedUrl(objectKey, expirationDate).toString();
		return ResponseEntity.ok(new UploadPreSignedUrlResponseDto(url, expirationDate, objectKey));
	}

	private String makeObjectKey(Member member, Purpose purpose, ImageExtension extension, String fileName) {
		return member.getMemberId().toString() + "/" + purpose.getDetails() + "/" + fileName + "."
			+ extension.getExtension();
	}
}
