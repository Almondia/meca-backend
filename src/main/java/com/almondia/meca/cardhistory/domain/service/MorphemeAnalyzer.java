package com.almondia.meca.cardhistory.domain.service;

import com.almondia.meca.cardhistory.infra.morpheme.Morphemes;
import com.almondia.meca.cardhistory.infra.morpheme.token.NlpToken;

public interface MorphemeAnalyzer<T extends NlpToken> {

	Morphemes<T> analyze(String cardAnswer, String userAnswer);
}
