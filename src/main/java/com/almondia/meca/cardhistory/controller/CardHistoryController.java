package com.almondia.meca.cardhistory.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.cardhistory.application.CardHistoryService;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryResponseDto;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
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
		cardHistoryService.saveCardHistories(saveRequestCardHistoryDto, member.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/cards/{cardId}")
	public ResponseEntity<CursorPage<CardHistoryResponseDto>> findCardHistoriesByCardId(
		@PathVariable("cardId") Id cardId,
		@RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
		@RequestParam(value = "hasNext", required = false) Id lastCardHistoryId
	) {
		CursorPage<CardHistoryWithCardAndMemberResponseDto> cursorPage = cardHistoryService.findCardHistoriesByCardId(
			cardId,
			pageSize, lastCardHistoryId);
		List<CardHistoryResponseDto> contents = cursorPage.getContents().stream()
			.map(this::convertHistory)
			.collect(Collectors.toList());
		CursorPage<CardHistoryResponseDto> result = new CursorPage<>(contents, cursorPage.getHasNext(),
			cursorPage.getPageSize(), cursorPage.getSortOrder());
		return ResponseEntity.ok(result);
	}

	@GetMapping("/members/{solvedMemberId}")
	public ResponseEntity<CursorPage<CardHistoryResponseDto>> findCardHistoriesBySolvedMemberId(
		@PathVariable("solvedMemberId") Id solvedMemberId,
		@RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
		@RequestParam(value = "hasNext", required = false) Id lastCardHistoryId
	) {
		CursorPage<CardHistoryWithCardAndMemberResponseDto> cursorPage = cardHistoryService.findCardHistoriesBySolvedMemberId(
			solvedMemberId,
			pageSize, lastCardHistoryId);
		List<CardHistoryResponseDto> contents = cursorPage.getContents().stream()
			.map(this::convertHistory)
			.collect(Collectors.toList());
		CursorPage<CardHistoryResponseDto> result = new CursorPage<>(contents, cursorPage.getHasNext(),
			cursorPage.getPageSize(), cursorPage.getSortOrder());
		return ResponseEntity.ok(result);
	}

	private CardHistoryResponseDto convertHistory(
		CardHistoryWithCardAndMemberResponseDto cardHistoryWithCardAndMemberResponseDto) {
		return CardHistoryResponseDto.builder()
			.cardHistoryId(cardHistoryWithCardAndMemberResponseDto.getCardHistory().getCardHistoryId())
			.solvedUserId(cardHistoryWithCardAndMemberResponseDto.getSolvedMember().getSolvedMemberId())
			.solvedUserName(cardHistoryWithCardAndMemberResponseDto.getSolvedMember().getSolvedMemberName())
			.userAnswer(cardHistoryWithCardAndMemberResponseDto.getCardHistory().getUserAnswer())
			.memberId(cardHistoryWithCardAndMemberResponseDto.getCard().getMemberId())
			.score(cardHistoryWithCardAndMemberResponseDto.getCardHistory().getScore())
			.cardId(cardHistoryWithCardAndMemberResponseDto.getCard().getCardId())
			.title(cardHistoryWithCardAndMemberResponseDto.getCard().getTitle().toString())
			.cardType(cardHistoryWithCardAndMemberResponseDto.getCard().getCardType())
			.question(cardHistoryWithCardAndMemberResponseDto.getCard().getQuestion())
			.answer(cardHistoryWithCardAndMemberResponseDto.getCard().getAnswer())
			.createdAt(cardHistoryWithCardAndMemberResponseDto.getCardHistory().getCreatedAt())
			.build();

	}
}
