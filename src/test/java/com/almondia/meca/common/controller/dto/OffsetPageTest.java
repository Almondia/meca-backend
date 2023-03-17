package com.almondia.meca.common.controller.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. offsetPage 내부 컨텐트는 읽기만 가능하고 수정이 불가능하다
 *
 */
class OffsetPageTest {

	@Test
	@DisplayName("offsetPage 내부 컨텐트는 읽기만 가능하고 수정이 불가능하다")
	void notModifyInnerContentsTest() {
		List<CardResponseDto> contents = List.of(
			CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build()
		);
		OffsetPage<CardResponseDto> offSetPage = OffsetPage.of(contents, 1, 3, 1);

		assertThatThrownBy(() -> {
			List<CardResponseDto> innerContents = offSetPage.getContents();
			innerContents.set(0, CardResponseDto.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title2"))
				.cardType(CardType.OX_QUIZ)
				.categoryId(Id.generateNextId())
				.answer("answer")
				.build());
		}).isInstanceOf(UnsupportedOperationException.class);
	}
}