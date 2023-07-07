package com.almondia.meca.cardhistory.infra.querydsl;

import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CardHistoryQueryDslRepository {

	CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize,
		Id lastCardHistoryId);

	CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesBySolvedMemberId(@NonNull Id solvedMemberId,
		int pageSize,
		Id lastCardHistoryId);

	Map<Id, Pair<Double, Long>> findCardHistoryScoresAvgAndCountsByCategoryIds(List<Id> categoryIds);
}
