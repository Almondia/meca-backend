package com.almondia.meca.cardhistory.domain.service;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.cardhistory.infra.morpheme.token.NlpToken;

public interface ScoringMachine<T extends NlpToken> {

	Score giveScore(MorphemeAnalyzer<T> morphemeAnalyzer, Card card, Answer userAnswer);
}
