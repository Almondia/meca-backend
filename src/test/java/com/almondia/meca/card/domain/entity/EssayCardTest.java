package com.almondia.meca.card.domain.entity;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.EssayAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class EssayCardTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("데이터 속성 생성 테스트")
	void oxCardAttributeCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(EssayCard.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("EssayCard");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("description", "memberId", "question", "isDeleted", "cardId", "categoryId",
				"title",
				"images",
				"createdAt",
				"modifiedAt", "essayAnswer");
	}

	@Test
	@DisplayName("영속성 가능 여부 테스트")
	void shouldPersistEntityTest() {
		EssayCard essayCard = EssayCard.builder()
			.cardId(Id.generateNextId())
			.memberId(Id.generateNextId())
			.categoryId(Id.generateNextId())
			.title(Title.of("title"))
			.description(Description.of("description"))
			.question(Question.of("question"))
			.essayAnswer(EssayAnswer.valueOf("keyword,Answer"))
			.build();
		entityManager.persist(essayCard);
		entityManager.flush();
		entityManager.clear();
		EssayCard findKeywordCard = entityManager.find(EssayCard.class, essayCard.getCardId());
		assertThat(findKeywordCard).isNotNull();
		assertThat(findKeywordCard.getTitle().toString()).isEqualTo("title");
		assertThat(findKeywordCard.getDescription().toString()).isEqualTo("description");
		assertThat(findKeywordCard.getQuestion().toString()).isEqualTo("question");
		assertThat(findKeywordCard.getEssayAnswer()).isEqualTo(EssayAnswer.valueOf("keyword,Answer"));
	}
}