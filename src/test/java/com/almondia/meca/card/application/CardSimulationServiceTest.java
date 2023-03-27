package com.almondia.meca.card.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.service.CategoryChecker;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.data.CardDataFactory;

class CardSimulationServiceTest {

	CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

	CategoryChecker categoryChecker = new CategoryChecker(categoryRepository);

	CardRepository cardRepository = Mockito.mock(CardRepository.class);

	@InjectMocks
	CardSimulationService cardSimulationService = new CardSimulationService(categoryChecker, cardRepository);

	@Test
	@DisplayName("simulateRandom 권한 체크 테스트")
	void randomCheckAuthorityTest() {
		Mockito.doReturn(Optional.empty()).when(categoryRepository).findByCategoryIdAndMemberId(any(), any());
		assertThatThrownBy(
			() -> cardSimulationService.simulateRandom(Id.generateNextId(), Id.generateNextId(), 100)).isInstanceOf(
			AccessDeniedException.class);
	}

	@Test
	@DisplayName("simulateRandom 응답 테스트")
	void randomResponseTest() {
		List<Card> testData = new CardDataFactory().createTestData();
		Mockito.doReturn(Optional.of(Category.builder().build()))
			.when(categoryRepository)
			.findByCategoryIdAndMemberId(any(), any());
		Mockito.doReturn(testData).when(cardRepository).findByCategoryId(any());
		final int limit = 3;

		List<CardResponseDto> randoms = cardSimulationService.simulateRandom(Id.generateNextId(), Id.generateNextId(),
			limit);
		assertThat(randoms).doesNotHaveDuplicates().hasSize(3);
	}

	@Test
	@DisplayName("simulateScore 권한 체크")
	void scoreCheckAuthorityTest() {
		Mockito.doReturn(Optional.empty()).when(categoryRepository).findByCategoryIdAndMemberId(any(), any());
		assertThatThrownBy(
			() -> cardSimulationService.simulateScore(Id.generateNextId(), Id.generateNextId(), 100)).isInstanceOf(
			AccessDeniedException.class);
	}

	@Test
	@DisplayName("simulateScore 정상 응답 테스트")
	void scoreResponseTest() {
		List<Card> testData = new CardDataFactory().createTestData();
		Mockito.doReturn(Optional.of(Category.builder().build()))
			.when(categoryRepository)
			.findByCategoryIdAndMemberId(any(), any());
		Mockito.doReturn(testData.subList(0, 3)).when(cardRepository).findCardByCategoryIdScoreAsc(any(), anyInt());
		final int limit = 3;

		List<CardResponseDto> scores = cardSimulationService.simulateScore(Id.generateNextId(), Id.generateNextId(),
			limit);
		assertThat(scores).doesNotHaveDuplicates().hasSize(3);
	}
}