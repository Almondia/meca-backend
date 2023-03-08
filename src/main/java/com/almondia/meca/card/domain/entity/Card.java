package com.almondia.meca.card.domain.entity;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import com.almondia.meca.card.domain.converter.ListImageConverter;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Image;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.domain.entity.DateEntity;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Card extends DateEntity {

	@EmbeddedId
	@AttributeOverride(name = "uuid", column = @Column(name = "card_id", nullable = false, columnDefinition = "BINARY(16)"))
	Id cardId;

	@Embedded
	@AttributeOverride(name = "question", column = @Column(name = "question", nullable = false, length = 500))
	Question question;

	@Embedded
	Title title;

	@Embedded
	@AttributeOverride(name = "uuid", column = @Column(name = "category_id", nullable = false, columnDefinition = "BINARY(16)"))
	Id categoryId;

	@Column(name = "images", length = 1020)
	@Convert(converter = ListImageConverter.class)
	private List<Image> images;

	@Transient
	private CardType cardType;

	private boolean isDeleted;

	public void delete() {
		isDeleted = true;
	}

	public void rollback() {
		isDeleted = false;
	}
}
