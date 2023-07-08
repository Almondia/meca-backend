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

	/**
	 * 카드 히스토리 통계와 카드 히스토리와 연결된 고유한 카드(문제를 푼 카드) 갯수를 쿼리해서 가져옴.
	 *
	 * @param categoryIds 카테고리 아이디 리스트
	 * @return 카테고리 아이디를 키로 하는 카드 히스토리 통계와 카드 히스토리와 연결된 고유한 카드(문제를 푼 카드) 갯수
	 */
	Map<Id, Pair<Double, Long>> findCardHistoryScoresAvgAndCountsByCategoryIds(List<Id> categoryIds);
}
