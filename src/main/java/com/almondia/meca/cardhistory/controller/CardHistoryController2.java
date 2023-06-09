package com.almondia.meca.cardhistory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.cardhistory.application.CardHistoryService;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/histories")
@RequiredArgsConstructor
public class CardHistoryController2 {

	private final CardHistoryService cardHistoryService;

	@GetMapping("/cards/{cardId}")
	public ResponseEntity<CursorPage<CardHistoryWithCardAndMemberResponseDto>> findCardHistoriesByCardId(
		@PathVariable("cardId") Id cardId,
		@RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
		@RequestParam(value = "hasNext", required = false) Id lastCardHistoryId
	) {
		CursorPage<CardHistoryWithCardAndMemberResponseDto> cursorPage = cardHistoryService.findCardHistoriesByCardId(
			cardId,
			pageSize, lastCardHistoryId);
		return ResponseEntity.ok(cursorPage);
	}

	@GetMapping("/members/{solvedMemberId}")
	public ResponseEntity<CursorPage<CardHistoryWithCardAndMemberResponseDto>> findCardHistoriesBySolvedMemberId(
		@PathVariable("solvedMemberId") Id solvedMemberId,
		@RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
		@RequestParam(value = "hasNext", required = false) Id lastCardHistoryId
	) {
		CursorPage<CardHistoryWithCardAndMemberResponseDto> cursorPage = cardHistoryService.findCardHistoriesBySolvedMemberId(
			solvedMemberId,
			pageSize, lastCardHistoryId);
		return ResponseEntity.ok(cursorPage);
	}

}
