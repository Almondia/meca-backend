package com.almondia.meca.common.domain.entity;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.TestPropertySource;

import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 1. 생성시 자동으로 날짜가 자동으로 생성되어야 함(생성일 == 수정일).
 * 2. 업데이트시 업데이트 날짜가 자동으로 업데이트 되어야 함(생성일 < 수정일).
 */
@DataJpaTest
@TestPropertySource(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class})
class DateEntityTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("영속시 생성일 날짜가 업데이트 됨 생성, 수정일 날짜가 같아야 한다.")
	void shouldUpdateCreatedAtPropertyWhenPersist() {
		Temp temp = new Temp(Id.generateNextId(), false);
		entityManager.persist(temp);
		assertThat(temp.getCreatedAt()).isNotNull();
		assertThat(temp.getModifiedAt()).isNotNull();
		assertThat(temp.getCreatedAt()).isEqualTo(temp.getModifiedAt());
	}

	@Test
	@DisplayName("영속화된 데이터를 꺼내서 수정하면 modifiedAt이 업데이트 수정 날짜가 생성 날짜보다 이 후여야 한다.")
	void test() throws InterruptedException {
		Temp temp = new Temp(Id.generateNextId(), false);
		JpaRepository<Temp, Id> tempRepository = new SimpleJpaRepository<>(Temp.class, entityManager);
		tempRepository.save(temp);
		Thread.sleep(200);
		Temp entity = tempRepository.findById(temp.getId()).orElseThrow();

		assertThat(entity.getModifiedAt()).isAfterOrEqualTo(entity.getCreatedAt());
	}

	@Entity
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Getter
	@Setter
	@ToString
	static class Temp extends DateEntity {
		@EmbeddedId
		private Id id;

		private boolean isActivate;
	}
}