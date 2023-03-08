package com.almondia.meca.card.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.sevice.CardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;

	@Secured("ROLE_USER")
	@PostMapping
	public ResponseEntity<CardResponseDto> saveCard(@RequestBody SaveCardRequestDto saveCardRequestDto) {
		CardResponseDto cardResponseDto = cardService.saveCard(saveCardRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
	}
}