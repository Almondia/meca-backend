package com.almondia.meca.cardhistory.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.controller.dto.CardStatisticsDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CardHistoryQueryDslRepository {

	CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesByCardId(@NonNull Id cardId, int pageSize,
		@Nullable Id lastCardHistoryId);

	CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesBySolvedMemberId(@NonNull Id solvedMemberId,
		int pageSize,
		@Nullable Id lastCardHistoryId);

	/**
	 * 카드 히스토리 통계와 카드 히스토리와 연결된 고유한 카드(문제를 푼 카드) 갯수를 쿼리해서 가져옴.
	 *
	 * @param categoryIds 카테고리 아이디 리스트
	 * @return 카테고리 아이디를 키로 하는 카드 히스토리 통계와 카드 히스토리와 연결된 고유한 카드(문제를 푼 카드) 갯수
	 */
	Map<Id, Pair<Double, Long>> findCardHistoryScoresAvgAndCountsByCategoryIds(List<Id> categoryIds);

	/**
	 * 카드 ID들의 정보를 이용해 카드 히스토리 통계와 카드 히스토리 갯수를 쿼리함
	 *
	 * @param cardIds 조회할 카드 리스트
	 * @return 카드 아이디를 키로 하는 카드 히스토리 통계와 카드 히스토리 갯수
	 */
	Map<Id, Pair<Double, Long>> findCardHistoryScoresAvgAndCountsByCardIds(List<Id> cardIds);

	/**
	 * 존재하는 카드에 대해서 해당 카드 ID에 카드 히스토리가 존재하는 경우 평균값과 카드 히스토리 갯수를 조회함
	 *
	 * @param cardId 조회하고자 하는 카드 ID
	 * @return 카드 히스토리가 존재하는 경우 평균값과 카드 히스토리 갯수, 존재하지 않는 경우 빈 Optional
	 */
	Optional<CardStatisticsDto> findCardHistoryScoresAvgAndCountsByCardId(Id cardId);
}
