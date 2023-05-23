package com.almondia.meca.recommand.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.almondia.meca.common.domain.entity.DateEntity;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CategoryRecommend extends DateEntity {

	@EmbeddedId
	@AttributeOverride(name = "uuid", column = @Column(name = "category_recommend_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id categoryRecommendId;

	@Embedded
	@AttributeOverride(name = "uuid", column = @Column(name = "category_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id categoryId;

	@Embedded
	@AttributeOverride(name = "uuid", column = @Column(name = "recommend__member_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id recommendMemberId;

	private boolean isDeleted;

	public void delete() {
		this.isDeleted = true;
	}

	public void restore() {
		this.isDeleted = false;
	}
}
