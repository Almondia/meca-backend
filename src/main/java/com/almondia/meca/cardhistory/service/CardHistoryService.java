package com.almondia.meca.cardhistory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almondia.meca.cardhistory.controller.dto.SaveRequestCardHistoryDto;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardHistoryService {

	private final CardHistoryRepository cardHistoryRepository;

	@Transactional
	public void saveHistories(SaveRequestCardHistoryDto saveRequestCardHistoryDto) {

	}
}
