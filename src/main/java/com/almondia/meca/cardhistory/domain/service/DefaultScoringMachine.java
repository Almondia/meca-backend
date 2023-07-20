package com.almondia.meca.cardhistory.domain.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.EssayCard;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.cardhistory.infra.morpheme.Morphemes;
import com.almondia.meca.cardhistory.infra.morpheme.token.NlpToken;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultScoringMachine implements ScoringMachine {

	@Override
	public Score giveScore(MorphemeAnalyzer<? extends NlpToken> morphemeAnalyzer, Card card, Answer userAnswer) {
		if (userAnswer.getText().isBlank()) {
			return new Score(0);
		}
		if (card.getCardType().equals(CardType.OX_QUIZ)) {
			return scoringOxCard(((OxCard)card), userAnswer);
		}
		if (card.getCardType().equals(CardType.KEYWORD)) {
			return scoringKeywordCard((KeywordCard)card, userAnswer);
		}
		if (card.getCardType().equals(CardType.MULTI_CHOICE)) {
			return scoringMultiChoiceCard((MultiChoiceCard)card, userAnswer);
		}
		if (card.getCardType().equals(CardType.ESSAY)) {
			return scoringEssayCard(morphemeAnalyzer, (EssayCard)card, userAnswer);
		}
		throw new IllegalArgumentException("지원하지 않는 카드 타입입니다.");
	}

	private Score scoringOxCard(OxCard card, Answer userAnswer) {
		if (card.getAnswer().equals(userAnswer.getText())) {
			return new Score(100);
		}
		return new Score(0);
	}

	private Score scoringKeywordCard(KeywordCard card, Answer userAnswer) {
		KeywordAnswer keywordAnswer = card.getKeywordAnswer();
		if (keywordAnswer.containsIgnoreCase(userAnswer.getText())) {
			return new Score(100);
		}
		return new Score(0);
	}

	private Score scoringMultiChoiceCard(MultiChoiceCard multiChoiceCard, Answer userAnswer) {
		MultiChoiceAnswer multiChoiceAnswer = multiChoiceCard.getMultiChoiceAnswer();
		if (multiChoiceAnswer.toString().equals(userAnswer.getText())) {
			return new Score(100);
		}
		return new Score(0);
	}

	private Score scoringEssayCard(MorphemeAnalyzer<? extends NlpToken> morphemeAnalyzer, EssayCard essayCard,
		Answer userAnswer) {
		Morphemes<? extends NlpToken> morphemes = morphemeAnalyzer.analyze(essayCard.getAnswer(), userAnswer.getText());
		List<String> cardAnswerMorpheme = morphemes.getCardAnswerMorpheme()
			.stream()
			.map(NlpToken::getMorph)
			.map(String::toLowerCase)
			.map(String::trim)
			.collect(toList());
		List<String> userAnswerMorpheme = morphemes.getUserAnswerMorpheme()
			.stream()
			.map(NlpToken::getMorph)
			.map(String::toLowerCase)
			.map(String::trim)
			.collect(toList());
		int totalSize = cardAnswerMorpheme.size();
		int correctSize = 0;
		for (String morph : userAnswerMorpheme) {
			if (cardAnswerMorpheme.contains(morph)) {
				correctSize++;
			}
		}
		if (totalSize == 0) {
			log.warn("EssayCard의 정답 형태소가 존재하지 않습니다. cardAnswerMorpheme: {}", cardAnswerMorpheme);
			return new Score(0);
		}
		return new Score(correctSize * 100 / totalSize);
	}
}
