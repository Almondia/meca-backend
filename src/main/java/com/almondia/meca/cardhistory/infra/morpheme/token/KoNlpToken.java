package com.almondia.meca.cardhistory.infra.morpheme.token;

import java.util.Objects;

import com.almondia.meca.cardhistory.infra.morpheme.MorphemePosition;

import lombok.Getter;

@Getter
public class KoNlpToken implements NlpToken {

	private final String morph;
	private final MorphemePosition pos;
	private final int beginIndex;
	private final int endIndex;

	public KoNlpToken(String morph, String pos, int beginIndex, int endIndex) {
		this.morph = morph;
		this.pos = MorphemePosition.valueOf(pos.toUpperCase());
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	public KoNlpToken(String morph, MorphemePosition pos, int beginIndex, int endIndex) {
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
		KoNlpToken koNlpToken = (KoNlpToken)o;
		return beginIndex == koNlpToken.beginIndex && endIndex == koNlpToken.endIndex && Objects.equals(morph,
			koNlpToken.morph)
			&& pos == koNlpToken.pos;
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
