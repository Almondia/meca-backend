package com.almondia.meca.card.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.infra.querydsl.CardQueryDslRepository;
import com.almondia.meca.common.domain.vo.Id;

public interface CardRepository extends JpaRepository<Card, Id>, CardQueryDslRepository {
	Optional<Card> findByCardIdAndMemberId(Id cardId, Id memberId);

	List<Card> findByCategoryIdAndIsDeleted(Id categoryId, boolean isDeleted);

}