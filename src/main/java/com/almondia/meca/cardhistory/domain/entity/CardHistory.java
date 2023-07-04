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
import com.almondia.meca.cardhistory.domain.vo.CardSnapShot;
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
	@AttributeOverride(name = "uuid", column = @Column(name = "card_history_id", nullable = false, length = 16))
	private Id cardHistoryId;

	@Embedded
	@AttributeOverride(name = "uuid", column = @Column(name = "solved_user_id", nullable = false, length = 16))
	private Id solvedMemberId;

	@Embedded
	@AttributeOverride(name = "uuid", column = @Column(name = "card_id", nullable = false, length = 16))
	private Id cardId;

	@Embedded
	@AttributeOverride(name = "answer", column = @Column(name = "user_answer", nullable = false, length = 2000))
	private Answer userAnswer;

	@Embedded
	@AttributeOverride(name = "score", column = @Column(name = "score", nullable = false))
	private Score score;

	@Embedded
	private CardSnapShot cardSnapShot;

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