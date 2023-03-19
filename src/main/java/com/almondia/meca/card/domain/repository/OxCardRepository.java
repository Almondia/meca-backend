package com.almondia.meca.card.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.common.domain.vo.Id;

public interface OxCardRepository extends JpaRepository<OxCard, Id> {
	Optional<OxCard> findByCardIdAndMemberId(Id cardId, Id memberId);

}