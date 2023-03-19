package com.almondia.meca.cardhistory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.service.CardHistoryService;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class CardHistoryController {

	private final CardHistoryService cardHistoryService;

	@PostMapping("/simulation")
	@Secured("ROLE_USER")
	public ResponseEntity<String> saveHistories(
		@AuthenticationPrincipal Member member,
		@RequestBody SaveRequestCardHistoryDto saveRequestCardHistoryDto) {
		cardHistoryService.saveHistories(saveRequestCardHistoryDto, member.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
