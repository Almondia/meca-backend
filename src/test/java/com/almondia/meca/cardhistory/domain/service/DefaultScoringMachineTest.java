package com.almondia.meca.cardhistory.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.cardhistory.infra.morpheme.Morphemes;
import com.almondia.meca.cardhistory.infra.morpheme.token.EngNlpToken;
import com.almondia.meca.cardhistory.infra.morpheme.token.NlpToken;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;

/**
 * 사용자 입력이 공백인 경우 0점을 출력한다
 * 카드 타입이 OX_QUIZ인 경우 정답이면 100점을 출력한다
 * 카드 타입이 OX_QUIZ인 경우 오답이면 0점을 출력한다
 * 카드 타입이 KEYWORD인 경우 정답이면 100점을 출력한다
 * 카드 타입이 KEYWORD인 경우 오답이면 0점을 출력한다
 * 카드 타입이 KEYWORD인 경우 대소문자와 관계없이 정답이면 100점을 출력한다
 * 카드 타입이 MULTI_CHOICE인 경우 정답이면 100점을 출력한다
 * 카드 타입이 MULTI_CHOICE인 경우 오답이면 0점을 출력한다
 * 카드 타입이 ESSAY인 경우 형태소에서 가져온 데이터가 없다면 0점을 출력한다
 * 카드 타입이 ESSAY인 경우 정답이면 100점을 출력한다
 * 카드 타입이 ESSAY인 경우 오답이면 0점을 출력한다
 */
@ExtendWith(MockitoExtension.class)
class DefaultScoringMachineTest {

	@Mock
	private MorphemeAnalyzer<? extends NlpToken> morphemeAnalyzer;
	private final ScoringMachine scoringMachine = new DefaultScoringMachine();

	@Test
	@DisplayName("사용자 입력이 공백인 경우 0점을 출력한다")
	void shouldReturnScoreZeroIfUserAnswerIsBlank() {
		// given
		Card oxCard = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, oxCard, new Answer("  "));

		// then
		assertThat(score).isEqualTo(new Score(0));
	}

	@Test
	@DisplayName("카드 타입이 OX_QUIZ인 경우 정답이면 100점을 출력한다")
	void shouldReturnScoreOneHundredIfCardTypeIsOxQuizAndAnswerIsCorrect() {
		// given
		Card oxCard = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, oxCard, new Answer("O"));

		// then
		assertThat(score).isEqualTo(new Score(100));
	}

	@Test
	@DisplayName("카드 타입이 OX_QUIZ인 경우 오답이면 0점을 출력한다")
	void shouldReturnScoreZeroIfCardTypeIsOxQuizAndAnswerIsIncorrect() {
		// given
		Card oxCard = CardTestHelper.genOxCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, oxCard, new Answer("X"));

		// then
		assertThat(score).isEqualTo(new Score(0));
	}

	@Test
	@DisplayName("카드 타입이 KEYWORD인 경우 정답이면 100점을 출력한다")
	void shouldReturnScoreOneHundredIfCardTypeIsKeywordAndAnswerIsCorrect() {
		// given
		Card keywordCard = CardTestHelper.genKeywordCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, keywordCard, new Answer("keyword"));

		// then
		assertThat(score).isEqualTo(new Score(100));
	}

	@Test
	@DisplayName("카드 타입이 KEYWORD인 경우 오답이면 0점을 출력한다")
	void shouldReturnScoreZeroIfCardTypeIsKeywordAndAnswerIsIncorrect() {
		// given
		Card keywordCard = CardTestHelper.genKeywordCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, keywordCard, new Answer("incorrect"));

		// then
		assertThat(score).isEqualTo(new Score(0));
	}

	@Test
	@DisplayName("카드 타입이 KEYWORD인 경우 대소문자와 관계없이 정답이면 100점을 출력한다")
	void shouldReturnScore100IfCardTypeIsKeywordAndAnswerIsCorrectRegardlessOfCase() {
		// given
		Card keywordCard = CardTestHelper.genKeywordCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
		keywordCard.changeAnswer("keyword,keyMap");
		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, keywordCard, new Answer("KEYWORD"));

		// then
		assertThat(score).isEqualTo(new Score(100));
	}

	@Test
	@DisplayName("카드 타입이 MULTI_CHOICE인 경우 정답이면 100점을 출력한다")
	void shouldReturnScoreOneHundredIfCardTypeIsMultiChoiceAndAnswerIsCorrect() {
		// given
		Card multiChoiceCard = CardTestHelper.genMultiChoiceCard(Id.generateNextId(), Id.generateNextId(),
			Id.generateNextId());

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, multiChoiceCard, new Answer("1"));

		// then
		assertThat(score).isEqualTo(new Score(100));
	}

	@Test
	@DisplayName("카드 타입이 MULTI_CHOICE인 경우 오답이면 0점을 출력한다")
	void shouldReturnScoreZeroIfCardTypeIsMultiChoiceAndAnswerIsIncorrect() {
		// given
		Card multiChoiceCard = CardTestHelper.genMultiChoiceCard(Id.generateNextId(), Id.generateNextId(),
			Id.generateNextId());

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, multiChoiceCard, new Answer("2"));

		// then
		assertThat(score).isEqualTo(new Score(0));
	}

	@Test
	@DisplayName("카드 타입이 ESSAY인 경우 형태소에서 가져온 데이터가 없다면 0점을 출력한다")
	void shouldReturnScoreZeroIfCardTypeIsEssayAndMorphemeAnalyzerReturnsEmpty() {
		// given
		Card essayCard = CardTestHelper.genEssayCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
		Mockito.when(morphemeAnalyzer.analyze(any(), any())).thenReturn(new Morphemes<>(List.of(), List.of()));

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, essayCard, new Answer("answer"));

		// then
		assertThat(score).isEqualTo(new Score(0));
	}

	@Test
	@DisplayName("카드 타입이 ESSAY인 경우 정답이면 100점을 출력한다")
	@SuppressWarnings("unchecked")
	void shouldReturnScoreOneHundredIfCardTypeIsEssayAndAnswerIsCorrect() {
		// given
		Card essayCard = CardTestHelper.genEssayCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
		Morphemes tokenMorphemes = new Morphemes(List.of(new EngNlpToken("answer", "NNP")),
			List.of(new EngNlpToken("answer", "NNP")));
		Mockito.when(morphemeAnalyzer.analyze(any(), any()))
			.thenReturn(tokenMorphemes);

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, essayCard, new Answer("answer"));

		// then
		assertThat(score).isEqualTo(new Score(100));
	}

	@Test
	@DisplayName("카드 타입이 ESSAY인 경우 오답이면 0점을 출력한다")
	@SuppressWarnings("unchecked")
	void shouldReturnScoreZeroIfCardTypeIsEssayAndAnswerIsIncorrect() {
		// given
		Card essayCard = CardTestHelper.genEssayCard(Id.generateNextId(), Id.generateNextId(), Id.generateNextId());
		Morphemes tokenMorphemes = new Morphemes(List.of(new EngNlpToken("answer", "NNP")),
			List.of(new EngNlpToken("aqw", "NNP")));
		Mockito.when(morphemeAnalyzer.analyze(any(), any()))
			.thenReturn(tokenMorphemes);

		// when
		Score score = scoringMachine.giveScore(morphemeAnalyzer, essayCard, new Answer("incorrect"));

		// then
		assertThat(score).isEqualTo(new Score(0));
	}
}