package com.almondia.meca.card.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.common.domain.vo.Id;

public interface MultiChoiceCardRepository extends JpaRepository<MultiChoiceCard, Id> {
	Optional<MultiChoiceCard> findByCardIdAndMemberId(Id cardId, Id memberId);

}