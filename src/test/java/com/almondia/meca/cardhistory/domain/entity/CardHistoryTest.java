package com.almondia.meca.cardhistory.domain.entity;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. 데이터 속성 생성 테스트
 * 2. delete 수행시 isDeleted = true
 * 3. rollbeck 수행시 isDeleted = false
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class CardHistoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("데이터 속성 생성 테스트")
	void oxCardAttributeCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(CardHistory.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("CardHistory");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("cardHistoryId", "solvedMemberId", "cardId", "userAnswer", "score",
				"cardSnapShot",
				"isDeleted", "createdAt");
	}

	@Test
	@DisplayName("delete 수행시 isDeleted = true")
	void shouldChangeTrueWhenCallDeleteTest() {
		CardHistory cardHistory = CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.score(new Score(100))
			.userAnswer(new Answer("answer"))
			.build();
		cardHistory.delete();
		assertThat(cardHistory).hasFieldOrPropertyWithValue("isDeleted", true);
	}

	@Test
	@DisplayName("rollbeck 수행시 isDeleted = false")
	void shouldChangeFalseWhenCallRollbackTest() {
		CardHistory cardHistory = CardHistory.builder()
			.cardHistoryId(Id.generateNextId())
			.score(new Score(100))
			.userAnswer(new Answer("answer"))
			.isDeleted(false)
			.build();
		cardHistory.rollback();
		assertThat(cardHistory).hasFieldOrPropertyWithValue("isDeleted", false);
	}

}