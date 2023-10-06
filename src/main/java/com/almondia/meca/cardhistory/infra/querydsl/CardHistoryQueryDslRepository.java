package com.almondia.meca.cardhistory.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;

import com.almondia.meca.cardhistory.controller.dto.CardHistoryWithCardAndMemberResponseDto;
import com.almondia.meca.cardhistory.controller.dto.CardStatisticsDto;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.domain.vo.Id;

public interface CardHistoryQueryDslRepository {

	/**
	 * 동일한 카드 ID를 가진 히스토리 내림차순 커서 페이징
	 *
	 * @param cardId            카드 아이디
	 * @param pageSize          페이지 사이즈
	 * @param lastCardHistoryId 마지막 카드 히스토리 아이디
	 * @return CardHistoryWithCardAndMemberResponseDto 기반 CursorPage
	 */
	CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesByCardId(Id cardId, int pageSize,
		@Nullable Id lastCardHistoryId);

	/**
	 * 동일한 마지막 푼 사용자를 가진 히스토리 내림차순 커서 페이징
	 *
	 * @param solvedMemberId    문제를 푼 사용자
	 * @param pageSize          페이지 사이즈
	 * @param lastCardHistoryId 마지막 카드 히스토리 아이디
	 * @return CardHistoryWithCardAndMemberResponseDto 기반 CursorPage
	 */
	CursorPage<CardHistoryWithCardAndMemberResponseDto> findCardHistoriesBySolvedMemberId(Id solvedMemberId,
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
	 * 카드 ID들의 정보를 이용해 카드 히스토리 통계와 카드 히스토리 갯수를 쿼리함.
	 * cardId의 카드 히스토리 정보가 없는 경우 Pair(0.0, 0L)으로 초기화 함.
	 *
	 * @param cardIds 조회할 카드 리스트
	 * @return 카드 아이디를 키로 하는 카드 히스토리 통계와 카드 히스토리 갯수
	 */
	Map<Id, Pair<Double, Long>> findCardHistoryScoresAvgAndCountsByCardIds(List<Id> cardIds);

	/**
	 * 카테고리 내의 카드들의 평균값들을 group by로 조회함
	 *
	 * @param categoryId 조회할 카테고리 ID
	 * @return 카드 아이디를 키로 하는 카드 점수 평균값
	 */
	Map<Id, Double> findCardScoreAvgMapByCategoryId(Id categoryId);

	/**
	 * 존재하는 카드에 대해서 해당 카드 ID에 카드 히스토리가 존재하는 경우 평균값과 카드 히스토리 갯수를 조회함
	 *
	 * @param cardId 조회하고자 하는 카드 ID
	 * @return 카드 히스토리가 존재하는 경우 평균값과 카드 히스토리 갯수, 존재하지 않는 경우 빈 Optional
	 */
	Optional<CardStatisticsDto> findCardHistoryScoresAvgAndCountsByCardId(Id cardId);
}
