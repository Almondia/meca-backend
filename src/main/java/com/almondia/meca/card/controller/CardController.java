package com.almondia.meca.card.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.sevice.CardService;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;

	@Secured("ROLE_USER")
	@PostMapping
	public ResponseEntity<CardResponseDto> saveCard(
		@AuthenticationPrincipal Member member,
		@RequestBody SaveCardRequestDto saveCardRequestDto) {
		CardResponseDto cardResponseDto = cardService.saveCard(saveCardRequestDto, member.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
	}

	@Secured("ROLE_USER")
	@PutMapping("/{cardId}")
	public ResponseEntity<CardResponseDto> updateCard(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "cardId") Id cardId,
		@RequestBody UpdateCardRequestDto updateCardRequestDto
	) {
		CardResponseDto responseDto = cardService.updateCard(updateCardRequestDto, cardId, member.getMemberId());
		return ResponseEntity.ok(responseDto);
	}
}