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

import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceQuestion;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;

/**
 * 1. 데이터 속성 생성 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class MultiChoiceCardTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("데이터 속성 생성 테스트")
	void oxCardAttributeCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(MultiChoiceCard.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("MultiChoiceCard");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("description", "memberId", "question", "isDeleted", "cardId", "categoryId",
				"title", "images", "createdAt", "modifiedAt", "multiChoiceAnswer");
	}

	@Test
	@DisplayName("changeQuestion 테스트")
	void changeQuestionTest() {
		MultiChoiceCard multiChoiceCard = MultiChoiceCard.builder()
			.question(MultiChoiceQuestion.of("[\"<p>question</p>\",\"1\",\"2\"]"))
			.multiChoiceAnswer(MultiChoiceAnswer.valueOf("2"))
			.build();
		multiChoiceCard.changeQuestion("[\"<p>question</p>\",\"1\",\"2\",\"3\"]");
		assertThat(multiChoiceCard.getQuestion()).isEqualTo("[\"<p>question</p>\",\"1\",\"2\",\"3\"]");
	}

	@Test
	@DisplayName("changeAnswer 테스트")
	void changeAnswerTest() {
		MultiChoiceCard multiChoiceCard = MultiChoiceCard.builder().build();
		multiChoiceCard.changeAnswer("2");
		assertThat(multiChoiceCard.getAnswer()).isEqualTo("2");
	}

	@Test
	@DisplayName("getAnswer 테스트")
	void shouldReturnStringAnswerTest() {
		MultiChoiceCard multiChoiceCard = MultiChoiceCard.builder()
			.multiChoiceAnswer(MultiChoiceAnswer.valueOf("2"))
			.build();
		assertThat(multiChoiceCard.getAnswer()).isEqualTo("2");
	}

}