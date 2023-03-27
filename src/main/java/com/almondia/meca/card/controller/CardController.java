package com.almondia.meca.card.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.card.application.CardService;
import com.almondia.meca.card.application.CardSimulationService;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.infra.querydsl.CardSearchCriteria;
import com.almondia.meca.card.infra.querydsl.CardSortField;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;
	private final CardSimulationService cardSimulationService;

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

	@Secured("ROLE_USER")
	@GetMapping("/categories/{categoryId}/me")
	public ResponseEntity<CursorPage<CardResponseDto>> searchPagingCards(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId,
		@RequestParam(value = "hasNext", required = false) Id lastId,
		@RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
		@RequestParam(value = "sortOrder", defaultValue = "desc") SortOrder sortOrder
	) {
		CardSearchCriteria criteria = makeCursorCriteria(categoryId, lastId, sortOrder);

		CursorPage<CardResponseDto> responseDto = cardService.searchCursorPagingCard(pageSize, categoryId, criteria,
			SortOption.of(CardSortField.CARD_ID, sortOrder), member.getMemberId());
		return ResponseEntity.ok(responseDto);
	}

	@Secured("ROLE_USER")
	@GetMapping("/{cardId}/me")
	public ResponseEntity<CardResponseDto> findCardByCardId(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "cardId") Id cardId
	) {
		CardResponseDto responseDto = cardService.findCardById(cardId, member.getMemberId());
		return ResponseEntity.ok(responseDto);
	}

	@Secured("ROLE_USER")
	@GetMapping("/categories/{categoryId}/simulation")
	public ResponseEntity<List<CardResponseDto>> findCardByCardIdResponseDto(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId,
		@RequestParam(value = "algorithm") String algorithm,
		@RequestParam(value = "limit") int limit
	) {
		if (algorithm.equals("random")) {
			List<CardResponseDto> random = cardSimulationService.simulateRandom(categoryId,
				member.getMemberId(), limit);
			return ResponseEntity.ok(random);
		}
		if (algorithm.equals("score")) {
			List<CardResponseDto> scores = cardSimulationService.simulateScore(categoryId, member.getMemberId(), limit);
			return ResponseEntity.ok(scores);
		}
		throw new IllegalArgumentException("algorithm: random, score 중 하나를 입력해주세요");
	}

	@Secured("ROLE_USER")
	@DeleteMapping("/{cardId}")
	public ResponseEntity<String> deleteCard(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "cardId") Id cardId
	) {
		cardService.deleteCard(cardId, member.getMemberId());
		return ResponseEntity.status(HttpStatus.OK).body("");
	}

	private CardSearchCriteria makeCursorCriteria(Id categoryId, Id lastId,
		SortOrder sortOrder) {
		Id gtLastId = null;
		Id ltLastId = null;
		if (sortOrder.equals(SortOrder.ASC)) {
			gtLastId = lastId;
		}
		if (sortOrder.equals(SortOrder.DESC)) {
			ltLastId = lastId;
		}
		return CardSearchCriteria.builder()
			.eqCategoryId(categoryId)
			.gtCardId(gtLastId)
			.ltCardId(ltLastId)
			.build();
	}
}