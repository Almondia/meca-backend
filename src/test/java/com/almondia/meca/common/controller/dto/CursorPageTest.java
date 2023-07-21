package com.almondia.meca.common.controller.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOrder;

/**
 * 1. cusorPage 내부 컨텐트는 읽기만 가능하고 수정이 불가능하다
 */
class CursorPageTest {

	@Test
	@DisplayName("cusorPage 내부 컨텐트는 읽기만 가능하고 수정이 불가능하다")
	void notModifyInnerContentsTest() {
		List<CardDto> contents = List.of(
			CardDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build()
		);
		CursorPage<CardDto> cursorPage = CursorPage.<CardDto>builder()
			.lastIdExtractStrategy(CardDto::getCardId)
			.pageSize(2)
			.contents(contents)
			.sortOrder(SortOrder.DESC)
			.build();

		assertThatThrownBy(() -> {
			List<CardDto> innerContents = cursorPage.getContents();
			innerContents.set(0, CardDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title2"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build());
		}).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	@DisplayName("cursorPage of로 lastId가 존재하는 contents인 경우 lastId가 자동으로 생성된다")
	void genLastIdWhenExistLastId() {
		// given
		final Id cardId1 = Id.generateNextId();
		final Id cardId2 = Id.generateNextId();
		final Id cardId3 = Id.generateNextId();

		List<CardDto> contents = List.of(
			CardDto.builder()
				.cardId(cardId3)
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build(),
			CardDto.builder()
				.cardId(cardId2)
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build(),
			CardDto.builder()
				.cardId(cardId1)
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build()
		);
		int pageSize = 2;
		SortOrder sortOrder = SortOrder.DESC;

		// when
		CursorPage<CardDto> cursorPage = CursorPage.<CardDto>builder()
			.lastIdExtractStrategy(CardDto::getCardId)
			.pageSize(pageSize)
			.contents(contents)
			.sortOrder(sortOrder)
			.build();

		// then
		assertThat(cursorPage.getContents()).hasSize(pageSize);
		assertThat(cursorPage.getHasNext()).isEqualTo(cardId1);
	}

	@Test
	@DisplayName("cursorPage of로 lastId가 존재하지 않는 contents인 경우 lastId가 null이다")
	void lastIdIsNullWhenNotExistLastId() {
		// given
		final Id cardId1 = Id.generateNextId();
		final Id cardId2 = Id.generateNextId();
		final Id cardId3 = Id.generateNextId();
		final int pageSize = 3;

		List<CardDto> contents = List.of(
			CardDto.builder()
				.cardId(cardId3)
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build(),
			CardDto.builder()
				.cardId(cardId2)
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build(),
			CardDto.builder()
				.cardId(cardId1)
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build()
		);

		SortOrder sortOrder = SortOrder.DESC;

		// when
		CursorPage<CardDto> cursorPage = CursorPage.<CardDto>builder()
			.lastIdExtractStrategy(CardDto::getCardId)
			.pageSize(pageSize)
			.contents(contents)
			.sortOrder(sortOrder)
			.build();

		// then
		assertThat(cursorPage.getContents()).hasSize(pageSize);
		assertThat(cursorPage.getHasNext()).isNull();
	}

}