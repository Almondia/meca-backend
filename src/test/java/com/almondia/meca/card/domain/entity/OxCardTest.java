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

import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;

/**
 * 1. 데이터 속성 생성 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfiguration.class})
class OxCardTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("데이터 속성 생성 테스트")
	void oxCardAttributeCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(OxCard.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("OxCard");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("description", "memberId", "question", "isDeleted", "cardId", "categoryId",
				"title",
				"images",
				"createdAt",
				"modifiedAt", "oxAnswer");
	}

	@Test
	@DisplayName("답변 수정 테스트")
	void shouldChangeAnswerTest() {
		// given
		OxCard oxCard = OxCard.builder()
			.question(Question.of("질문"))
			.oxAnswer(OxAnswer.O)
			.build();

		// when
		oxCard.changeAnswer("X");

		// then
		assertThat(oxCard.getAnswer()).isEqualTo("X");
	}
}