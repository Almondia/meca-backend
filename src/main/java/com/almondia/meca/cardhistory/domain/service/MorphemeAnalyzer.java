package com.almondia.meca.cardhistory.domain.service;

import java.util.List;

import com.almondia.meca.cardhistory.domain.vo.NlpToken;

public interface MorphemeAnalyzer {

	List<NlpToken> analyze(String text);
}
