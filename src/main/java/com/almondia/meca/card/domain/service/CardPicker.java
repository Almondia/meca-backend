package com.almondia.meca.card.domain.service;

import java.util.List;

import com.almondia.meca.card.domain.entity.Card;

public interface CardPicker {

	List<Card> pick(List<Card> cards, int limit);
}
