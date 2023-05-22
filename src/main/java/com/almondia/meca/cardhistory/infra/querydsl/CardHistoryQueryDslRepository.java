package com.almondia.meca.cardhistory.infra.querydsl;

import org.springframework.lang.NonNull;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryResponseDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CardHistoryQueryDslRepository {

	CursorPage<CardHistoryResponseDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize,
		Id lastCardHistoryId);

	CursorPage<CardHistoryResponseDto> findCardHistoriesByCategoryId(@NonNull Id categoryId, int pageSize,
		Id lastCardHistoryId);

	CursorPage<CardHistoryResponseDto> findCardHistoriesBySolvedMemberId(@NonNull Id solvedMemberId, int pageSize,
		Id lastCardHistoryId);
}
