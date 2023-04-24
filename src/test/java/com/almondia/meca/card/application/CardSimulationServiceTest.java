package com.almondia.meca.card.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;
import com.almondia.meca.helper.CategoryTestHelper;

class CardSimulationServiceTest {

	CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

	CategoryChecker categoryChecker = Mockito.mock(CategoryChecker.class);

	CardRepository cardRepository = Mockito.mock(CardRepository.class);

	@InjectMocks
	CardSimulationService cardSimulationService = new CardSimulationService(categoryChecker, cardRepository);

	@Test
	@DisplayName("simulateRandom 권한 체크 테스트")
	void randomCheckAuthorityTest() {
		Mockito.doThrow(AccessDeniedException.class).when(categoryChecker).checkAuthority(any(), any());
		assertThatThrownBy(
			() -> cardSimulationService.simulateRandom(Id.generateNextId(), Id.generateNextId(), 100)).isInstanceOf(
			AccessDeniedException.class);
	}

	@Test
	@DisplayName("simulateRandom 응답 테스트")
	void randomResponseTest() {
		Id memberId = Id.generateNextId();
		Id categoryId = Id.generateNextId();
		List<Card> testData = List.of(
			CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()),
			CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()));
		Mockito.doReturn(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId))
			.when(categoryChecker).checkAuthority(any(), any());
		Mockito.doReturn(testData).when(cardRepository).findByCategoryIdAndIsDeleted(any(), eq(false));
		final int limit = 2;

		List<CardResponseDto> randoms = cardSimulationService.simulateRandom(Id.generateNextId(), Id.generateNextId(),
			limit);
		assertThat(randoms).doesNotHaveDuplicates().hasSize(2);
	}

	@Test
	@DisplayName("simulateScore 권한 체크")
	void scoreCheckAuthorityTest() {
		Mockito.doThrow(AccessDeniedException.class).when(categoryChecker).checkAuthority(any(), any());
		assertThatThrownBy(
			() -> cardSimulationService.simulateScore(Id.generateNextId(), Id.generateNextId(), 100)).isInstanceOf(
			AccessDeniedException.class);
	}

	@Test
	@DisplayName("simulateScore 정상 응답 테스트")
	void scoreResponseTest() {
		Id memberId = Id.generateNextId();
		Id categoryId = Id.generateNextId();
		List<Card> testData = List.of(
			CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()),
			CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()),
			CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId()),
			CardTestHelper.genOxCard(memberId, categoryId, Id.generateNextId())
		);
		Mockito.doReturn(CategoryTestHelper.generateUnSharedCategory("title", memberId, categoryId))
			.when(categoryChecker).checkAuthority(any(), any());
		Mockito.doReturn(testData).when(cardRepository).findCardByCategoryIdScoreAsc(any(), anyInt());
		final int limit = 4;

		List<CardResponseDto> scores = cardSimulationService.simulateScore(Id.generateNextId(), Id.generateNextId(),
			limit);
		assertThat(scores).doesNotHaveDuplicates().hasSize(4);
	}
}