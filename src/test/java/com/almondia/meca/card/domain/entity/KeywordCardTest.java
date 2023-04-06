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

import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;

/**
 * 데이터 속성 생성 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class KeywordCardTest {
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("데이터 속성 생성 테스트")
	void oxCardAttributeCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(KeywordCard.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("KeywordCard");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("description", "memberId", "question", "isDeleted", "cardId", "categoryId",
				"title",
				"images",
				"createdAt",
				"modifiedAt", "keywordAnswer");
	}
}