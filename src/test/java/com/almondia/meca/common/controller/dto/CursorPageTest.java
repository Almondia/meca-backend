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
			.pageSize(2)
			.contents(contents)
			.hasNext(contents.get(0).getCardId())
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

}