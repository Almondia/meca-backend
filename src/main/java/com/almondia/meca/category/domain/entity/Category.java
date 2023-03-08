package com.almondia.meca.category.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.domain.entity.DateEntity;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends DateEntity {

	@EmbeddedId
	@AttributeOverride(name = "uuid", column = @Column(name = "category_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id categoryId;

	@Embedded
	@AttributeOverride(name = "title", column = @Column(name = "title", nullable = false, length = 60))
	private Title title;

	private boolean isDeleted;

	private boolean isShared;

	public void delete() {
		this.isDeleted = true;
	}

	public void rollback() {
		this.isDeleted = false;
	}

	public void changeShare(boolean isShared) {
		this.isShared = isShared;
	}
}
