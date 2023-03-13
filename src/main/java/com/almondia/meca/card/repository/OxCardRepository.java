package com.almondia.meca.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.common.domain.vo.Id;

public interface OxCardRepository extends JpaRepository<OxCard, Id> {

}