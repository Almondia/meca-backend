package com.almondia.meca.cardhistory.domain.vo;

import java.util.Objects;

import com.almondia.meca.cardhistory.infra.morpheme.MorphemePosition;

import lombok.Getter;

@Getter
public class NlpToken {

	private final String morph;
	private final MorphemePosition pos;
	private final int beginIndex;
	private final int endIndex;

	public NlpToken(String morph, String pos, int beginIndex, int endIndex) {
		this.morph = morph;
		this.pos = MorphemePosition.valueOf(pos.toUpperCase());
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	public NlpToken(String morph, MorphemePosition pos, int beginIndex, int endIndex) {
		this.morph = morph;
		this.pos = pos;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		NlpToken nlpToken = (NlpToken)o;
		return beginIndex == nlpToken.beginIndex && endIndex == nlpToken.endIndex && Objects.equals(morph,
			nlpToken.morph)
			&& pos == nlpToken.pos;
	}

	@Override
	public int hashCode() {
		return Objects.hash(morph, pos, beginIndex, endIndex);
	}

	@Override
	public String toString() {
		return "NlpToken{" +
			"morph='" + morph + '\'' +
			", pos=" + pos +
			", beginIndex=" + beginIndex +
			", endIndex=" + endIndex +
			'}';
	}
}
