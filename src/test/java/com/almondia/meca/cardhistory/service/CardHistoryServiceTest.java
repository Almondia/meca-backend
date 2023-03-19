package com.almondia.meca.cardhistory.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.data.CardDataFactory;

/**
 * 1. 권한 체크 여부 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class, CardHistoryService.class})
class CardHistoryServiceTest {

	@Autowired
	CardRepository cardRepository;

	@Autowired
	CardHistoryService cardHistoryService;

	CardDataFactory cardDataFactory = new CardDataFactory();

	@BeforeEach
	void before() {
		List<Card> testData = cardDataFactory.createTestData();
		cardRepository.saveAll(testData);
	}

	@Test
	@DisplayName("권한 체크 여부 테스트")
	void checkAuthorityTest() {
		SaveRequestCardHistoryDto saveRequestCardHistoryDto = new SaveRequestCardHistoryDto(List.of(
			CardHistoryDto.builder()
				.cardId(Id.generateNextId())
				.score(new Score(100))
				.userAnswer(new Answer("100"))
				.build()));
		assertThatThrownBy(
			() -> cardHistoryService.saveHistories(saveRequestCardHistoryDto, Id.generateNextId())).isInstanceOf(
			IllegalArgumentException.class);
	}
}