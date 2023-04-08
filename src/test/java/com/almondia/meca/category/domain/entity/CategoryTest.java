package com.almondia.meca.category.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;

/**
 * 1. 엔티티 속성이 에러 없이 필요한 속성이 잘 생성 되었는지 테스트
 * 2. 엔티티 생성시 생성일과 수정일이 동일하게 생성되는지 검증
 * 3. 엔티티 수정시 수정일이 수정되고 생성일보다 이후 날짜가 되야 한다
 * 4. 삭제 메서드 호출시 삭제 상태가 된다
 * 5. 롤백 메서드 호추시 삭제 상태는 false가 된다
 * 6. 공유 변경 메서드를 통해 isShared의 상태를 변경할 수 있다
 * 7. 타이틀 변경 요청시 해당 타이틀로 변형된다
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class CategoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("엔티티 속성이 에러 없이 필요한 속성이 잘 생성 되었는지 테스트")
	void categoryCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(Category.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("Category");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("memberId", "categoryId", "title", "thumbnail", "isDeleted", "isShared",
				"createdAt",
				"modifiedAt");
	}

	@Test
	@DisplayName("엔티티 생성시 생성일과 수정일이 동일하게 생성되는지 검증")
	void shouldUpdateCreatedAtAndEqualModifiedAtWhenEntitySave() {
		JpaRepository<Category, Id> categoryRepository = new SimpleJpaRepository<>(Category.class, entityManager);
		Category category = Category.builder()
			.categoryId(Id.generateNextId())
			.memberId(Id.generateNextId())
			.title(new Title("title"))
			.build();
		categoryRepository.saveAndFlush(category);
		Category result = categoryRepository.findById(category.getCategoryId()).orElseThrow();
		LocalDateTime createdAt = result.getCreatedAt();
		LocalDateTime modifiedAt = result.getModifiedAt();
		assertThat(createdAt).isEqualTo(modifiedAt);
	}

	@Test
	@DisplayName("엔티티 수정시 수정일이 수정되고 생성일보다 이후 날짜가 되야 한다")
	void shouldUpdateModifiedAtAndAfterThanCreatedAtWhenEntityModifiedTest() throws InterruptedException {
		JpaRepository<Category, Id> categoryRepository = new SimpleJpaRepository<>(Category.class, entityManager);
		Category category = Category.builder()
			.categoryId(Id.generateNextId())
			.memberId(Id.generateNextId())
			.title(new Title("title"))
			.build();
		Category temp = categoryRepository.save(category);
		temp.delete();
		categoryRepository.saveAndFlush(temp);
		Thread.sleep(100);
		Category result = categoryRepository.findById(category.getCategoryId()).orElseThrow();
		LocalDateTime createdAt = result.getCreatedAt();
		LocalDateTime modifiedAt = result.getModifiedAt();
		assertThat(modifiedAt).isAfter(createdAt);
	}

	@Test
	@DisplayName("삭제 메서드 호출시 삭제 상태가 된다")
	void shouldIsDeleteTrueWhenCallDeleteTest() {
		Category category = Category.builder()
			.categoryId(Id.generateNextId())
			.title(new Title("title"))
			.build();
		category.delete();
		assertThat(category).hasFieldOrPropertyWithValue("isDeleted", true);
	}

	@Test
	@DisplayName("롤백 메서드 호추시 삭제 상태는 false가 된다")
	void shouldIsDeletedFalseWhenCallCallbackTest() {
		Category category = Category.builder()
			.categoryId(Id.generateNextId())
			.title(new Title("title"))
			.isDeleted(true)
			.build();
		category.rollback();
		assertThat(category).hasFieldOrPropertyWithValue("isDeleted", false);
	}

	@Test
	@DisplayName("공유 변경 메서드를 통해 isShared의 상태를 변경할 수 있다")
	void shouldChangeIsSharedWhenCallChangeShareTest() {
		Category category = Category.builder()
			.categoryId(Id.generateNextId())
			.title(new Title("title"))
			.isDeleted(true)
			.build();

		category.changeShare(true);
		assertThat(category).hasFieldOrPropertyWithValue("isShared", true);
	}

	@Test
	@DisplayName("타이틀 변경 요청시 해당 타이틀로 변형된다")
	void shouldChangeTitleWhenCallChangeTitleTest() {
		Category category = Category.builder()
			.categoryId(Id.generateNextId())
			.title(new Title("title"))
			.isDeleted(true)
			.build();
		Title newTitle = new Title("x");
		category.changeTitle(newTitle);
		assertThat(category).hasFieldOrPropertyWithValue("title", newTitle);
	}

}