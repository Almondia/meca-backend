package com.almondia.meca.card.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.almondia.meca.card.domain.entity.Card;

public class RandomCardPicker implements CardPicker {

	@Override
	public List<Card> pick(List<Card> cards, int limit) {
		if (cards.size() <= limit) {
			return new ArrayList<>(cards);
		}
		Random random = new Random();
		int[] indices = IntStream.range(0, cards.size()).toArray();
		List<Card> randomList = new ArrayList<>();
		for (int i = 0; i < limit; ++i) {
			int randomIndex = random.nextInt(cards.size() - i);
			randomList.add(cards.get(indices[randomIndex]));
			indices[randomIndex] = indices[cards.size() - 1 - i];
		}
		return randomList;
	}
}
