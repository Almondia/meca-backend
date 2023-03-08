package com.almondia.meca.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.common.domain.vo.Id;

public interface KeywordCardRepository extends JpaRepository<KeywordCard, Id> {
}