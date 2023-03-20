package com.almondia.meca.cardhistory.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.common.domain.vo.Id;

public interface CardHistoryRepository extends JpaRepository<CardHistory, Id> {
	List<CardHistory> findByCardId(Id cardId);
}