package com.almondia.meca.recommand.domain.entity;

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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class CategoryRecommendTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("엔티티 속성이 에러 없이 필요한 속성이 잘 생성 되었는지 테스트")
	void categoryCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(CategoryRecommend.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("CategoryRecommend");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("categoryRecommendId", "categoryId", "recommendMemberId", "isDeleted",
				"createdAt", "modifiedAt");
	}
}