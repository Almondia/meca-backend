package com.almondia.meca.cardhistory.infra.querydsl;

import org.springframework.lang.NonNull;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CardHistoryQueryDslRepository {

	CursorPage<CardHistoryDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize, Id lastCardHistoryId);

	CursorPage<CardHistoryDto> findCardHistoriesByCategoryId(@NonNull Id categoryId, int pageSize,
		Id lastCardHistoryId);
}
