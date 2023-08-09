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
import com.almondia.meca.card.controller.dto.CardCountGroupByScoreDto;
import com.almondia.meca.card.controller.dto.CardCountResponseDto;
import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.controller.dto.CardWithStatisticsDto;
import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.controller.dto.UpdateCardRequestDto;
import com.almondia.meca.card.infra.querydsl.CardSearchOption;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;
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
	public ResponseEntity<CardDto> saveCard(
		@AuthenticationPrincipal Member member,
		@RequestBody SaveCardRequestDto saveCardRequestDto) {
		CardDto cardDto = cardService.saveCard(saveCardRequestDto, member.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cardDto);
	}

	@Secured("ROLE_USER")
	@PutMapping("/{cardId}")
	public ResponseEntity<CardDto> updateCard(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "cardId") Id cardId,
		@RequestBody UpdateCardRequestDto updateCardRequestDto
	) {
		CardDto responseDto = cardService.updateCard(updateCardRequestDto, cardId, member.getMemberId());
		return ResponseEntity.ok(responseDto);
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

	@Secured("ROLE_USER")
	@GetMapping("/{cardId}/me")
	public ResponseEntity<CardResponseDto> findCardByCardId(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "cardId") Id cardId
	) {
		CardDto responseDto = cardService.findCardById(cardId, member.getMemberId());
		return ResponseEntity.ok(new CardResponseDto(responseDto, member));
	}

	@GetMapping("/{cardId}/share")
	public ResponseEntity<CardResponseDto> findCardByCardId(
		@PathVariable(value = "cardId") Id cardId
	) {
		CardResponseDto responseDto = cardService.findSharedCard(cardId);
		return ResponseEntity.ok(responseDto);
	}

	@Secured("ROLE_USER")
	@GetMapping("/categories/{categoryId}/me")
	public ResponseEntity<CursorPage<CardWithStatisticsDto>> searchPagingCards(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId,
		@RequestParam(value = "hasNext", required = false) Id lastCardId,
		@RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
		@RequestParam(value = "containTitle", required = false) String containTitle
	) {
		CardSearchOption cardSearchOption = CardSearchOption.builder()
			.containTitle(containTitle)
			.build();

		CursorPage<CardWithStatisticsDto> responseDto = cardService.searchCursorPagingCard(
			pageSize, lastCardId, categoryId, member, cardSearchOption);
		return ResponseEntity.ok(responseDto);
	}

	@GetMapping("/categories/{categoryId}/share")
	public ResponseEntity<CursorPage<CardWithStatisticsDto>> searchSharedCardPaging(
		@PathVariable("categoryId") Id categoryId,
		@RequestParam(value = "hasNext", required = false) Id lastCardId,
		@RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
		@RequestParam(value = "containTitle", required = false) String containTitle
	) {
		CardSearchOption cardSearchOption = CardSearchOption.builder()
			.containTitle(containTitle)
			.build();

		CursorPage<CardWithStatisticsDto> responseDto = cardService.searchCursorPagingSharedCard(
			pageSize, lastCardId, categoryId, cardSearchOption);
		return ResponseEntity.ok(responseDto);
	}

	@Secured("ROLE_USER")
	@GetMapping("/categories/{categoryId}/simulation")
	public ResponseEntity<List<CardDto>> findCardByCardIdResponseDto(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId,
		@RequestParam(value = "algorithm") String algorithm,
		@RequestParam(value = "limit") int limit
	) {
		if (algorithm.equals("random")) {
			List<CardDto> random = cardSimulationService.simulateRandom(categoryId,
				member.getMemberId(), limit);
			return ResponseEntity.ok(random);
		}
		if (algorithm.equals("score")) {
			List<CardDto> scores = cardSimulationService.simulateScore(categoryId, member.getMemberId(), limit);
			return ResponseEntity.ok(scores);
		}
		throw new IllegalArgumentException("algorithm: random, score 중 하나를 입력해주세요");
	}

	@GetMapping("/categories/{categoryId}/simulation/before/count")
	public ResponseEntity<List<CardCountGroupByScoreDto>> findCardCountByCategoryId(
		@PathVariable(value = "categoryId") Id categoryId
	) {
		List<CardCountGroupByScoreDto> cardCountGroupByScoreDtos = cardSimulationService.findCardCountByScore(
			categoryId);
		return ResponseEntity.ok(cardCountGroupByScoreDtos);
	}

	@Secured("ROLE_USER")
	@GetMapping("/categories/{categoryId}/me/count")
	public ResponseEntity<CardCountResponseDto> countCards(
		@AuthenticationPrincipal Member member,
		@PathVariable(value = "categoryId") Id categoryId
	) {
		long count = cardService.findCardsCountByCategoryId(categoryId, member.getMemberId());
		return ResponseEntity.ok(new CardCountResponseDto(count));
	}
}