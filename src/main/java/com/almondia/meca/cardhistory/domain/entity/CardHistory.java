package com.almondia.meca.cardhistory.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "card_history")
@Getter
public class CardHistory {

	@EmbeddedId
	@AttributeOverride(name = "uuid", column = @Column(name = "card_history_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id cardHistoryId;

	@Embedded
	@AttributeOverride(name = "uuid", column = @Column(name = "card_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id cardId;

	@Embedded
	@AttributeOverride(name = "uuid", column = @Column(name = "category_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id categoryId;

	@Embedded
	@AttributeOverride(name = "answer", column = @Column(name = "user_answer", nullable = false, length = 25))
	private Answer userAnswer;

	@Embedded
	@AttributeOverride(name = "score", column = @Column(name = "score", nullable = false))
	private Score score;

	private boolean isDeleted;

	@CreatedDate
	private LocalDateTime createdAt;

	public void delete() {
		isDeleted = true;
	}

	public void rollback() {
		isDeleted = false;
	}

}