package com.almondia.meca.card.infra.querydsl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.lang.Nullable;

import com.almondia.meca.card.controller.dto.CardDto;
import com.almondia.meca.card.controller.dto.CardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.common.domain.vo.Id;

public interface CardQueryDslRepository {

	/**
	 * 카테고리 ID를 활용해 카드를 페이징 조회 (삭제된 카드는 제외)
	 *
	 * @param pageSize         페이징 사이즈
	 * @param lastCardId       마지막 카드 ID
	 * @param categoryId       카테고리 ID
	 * @param cardSearchOption 카드 검색 옵션
	 * @return 카드 리스트
	 */
	List<CardDto> findCardByCategoryId(
		int pageSize, @Nullable Id lastCardId, Id categoryId, CardSearchOption cardSearchOption);

	/**
	 * 공유 카테고리에 속한 카드를 단일 조회
	 *
	 * @param cardId 조회할 카드 ID
	 * @return 카드 정보 (카드 정보와 회원 정보를 포함함)
	 */
	Optional<CardResponseDto> findCardInSharedCategory(Id cardId);

	/**
	 * 하나의 카테고리에 속한 삭제되지 않은 모든 카드들의 갯수를 조회
	 *
	 * @param categoryId 카테고리 ID
	 * @return 카드 갯수
	 */
	long countCardsByCategoryId(Id categoryId);

	/**
	 * 카테고리 ID별로 속한 카드들의 갯수를 조회
	 *
	 * @param categoryIds 카테고리 ID 리스트
	 * @return 카테고리 ID별 카드 갯수
	 */
	Map<Id, Long> countCardsByCategoryIdIsDeletedFalse(List<Id> categoryIds);

	/**
	 * 평균 점수 기준으로 오름차순으로 정렬된 카드 리스트 조회
	 *
	 * @param categoryId 카테고리 ID
	 * @param limit      최대 제한 조회 갯수
	 * @return 평균 점수 기준으로 오름차 순으로 정렬된 카드 리스트
	 */
	List<Card> findCardByCategoryIdScoreAsc(Id categoryId, int limit);

	Map<Id, List<Id>> findMapByListOfCardIdAndMemberId(List<Id> cardIds, Id memberId);
}
