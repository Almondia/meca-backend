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

@Component
public class DefaultScoringMachine implements ScoringMachine<NlpToken> {

	@Override
	public Score giveScore(MorphemeAnalyzer<NlpToken> morphemeAnalyzer, Card card, Answer userAnswer) {
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
		if (card.getAnswer().equals(userAnswer.toString())) {
			return new Score(100);
		}
		return new Score(0);
	}

	private Score scoringKeywordCard(KeywordCard card, Answer userAnswer) {
		KeywordAnswer keywordAnswer = card.getKeywordAnswer();
		if (keywordAnswer.contains(userAnswer.toString())) {
			return new Score(100);
		}
		return new Score(0);
	}

	private Score scoringMultiChoiceCard(MultiChoiceCard multiChoiceCard, Answer userAnswer) {
		MultiChoiceAnswer multiChoiceAnswer = multiChoiceCard.getMultiChoiceAnswer();
		if (multiChoiceAnswer.toString().equals(userAnswer.toString())) {
			return new Score(100);
		}
		return new Score(0);
	}

	private Score scoringEssayCard(MorphemeAnalyzer<NlpToken> morphemeAnalyzer, EssayCard essayCard,
		Answer userAnswer) {
		Morphemes<NlpToken> morphemes = morphemeAnalyzer.analyze(essayCard.getAnswer(), userAnswer.getText());
		List<String> cardAnswerMorpheme = morphemes.getCardAnswerMorpheme()
			.stream()
			.map(NlpToken::getMorph)
			.collect(toList());
		List<String> userAnswerMorpheme = morphemes.getUserAnswerMorpheme()
			.stream()
			.map(NlpToken::getMorph)
			.collect(toList());
		int totalSize = cardAnswerMorpheme.size();
		int correctSize = 0;
		for (String morph : userAnswerMorpheme) {
			if (cardAnswerMorpheme.contains(morph)) {
				correctSize++;
			}
		}
		return new Score(correctSize * 100 / totalSize);
	}
}
