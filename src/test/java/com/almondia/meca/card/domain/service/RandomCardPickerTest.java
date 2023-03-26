package com.almondia.meca.card.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.data.CardDataFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 1. limit과 card 입력 size가 같은 경우 복사해서 리턴한다
 * 2. 2자리수 데이터는 pick 메서드가 10ms 이내에 실행되야 함
 */
@Slf4j
class RandomCardPickerTest {

	@Test
	@DisplayName("limit과 card 입력 size가 같은 경우 복사해서 리턴한다")
	void shouldCopySameListWhenCardSizeLoeLimitTest() {
		CardDataFactory cardDataFactory = new CardDataFactory();
		List<Card> testData = cardDataFactory.createTestData();
		CardPicker cardPicker = new RandomCardPicker();
		List<Card> pick = cardPicker.pick(testData, testData.size());
		assertThat(pick).hasSize(testData.size());
	}

	@RepeatedTest(value = 10)
	@DisplayName("2자리수 데이터는 pick 메서드가 10ms 이내에 실행되야 함")
	void shoutTest() {
		CardDataFactory cardDataFactory = new CardDataFactory();
		List<Card> testData = cardDataFactory.createTestData();
		CardPicker cardPicker = new RandomCardPicker();
		assertTimeoutPreemptively(Duration.ofMillis(10L), () -> {
			List<Card> pick = cardPicker.pick(testData, 3);
			log.info(String.valueOf(pick));
		});

	}
}