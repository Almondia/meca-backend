package com.almondia.meca.category.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.entity.QCategory;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 1. title 설정시 해당 글자로 시작하는 문자의 데이터를 가져온다
 * 2. 생성일 시작 설정시 생성 시작일부터 그 이후 데이터만 가져온다
 * 3. 생성일 종료 설정시 생성일 종료일 이전 데이터만 가져온다
 * 4. 수정일 시작 설정시 생성 시작일부터 그 이후 데이터만 가져온다
 * 5. 수정일 종료 설정시 생성 종료일 이전 데이터만 가져온다
 * 6. 공유 데이터 설정시 공유 데이터만 가져와야 한다
 * 7. 삭제 데이터 설정시 삭제 설정된 데이터만 가져와야 한다
 * 8. 일치하는 회원 아이디 설정시 해당 회원 아이디만 조회해야 한다
 * 9. 2개 이상의 쿼리 옵션 가능 여부 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfiguration.class, JpaAuditingConfiguration.class})
class CategorySearchCriteriaTest {

	@Autowired
	JPAQueryFactory queryFactory;

	@Autowired
	CategoryRepository categoryRepository;

	QCategory category = QCategory.category;

	LocalDateTime now = LocalDateTime.now();
	Id myId = Id.generateNextId();

	Id toModifyId = Id.generateNextId();

	@BeforeEach
	void before() {
		List<Category> categories = List.of(
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(myId)
				.createdAt(now)
				.modifiedAt(now)
				.title(new Title("btitle1")).build(),
			Category.builder()
				.categoryId(toModifyId)
				.memberId(myId)
				.createdAt(now.plusHours(4))
				.modifiedAt(now.plusHours(4))
				.title(new Title("atitle2")).build(),
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(myId)
				.createdAt(now.plusHours(2))
				.modifiedAt(now.plusHours(2))
				.title(new Title("ctitle")).build(),
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.createdAt(now)
				.modifiedAt(now.plusHours(3))
				.title(new Title("dTitle"))
				.isShared(true).build(),
			Category.builder()
				.categoryId(Id.generateNextId())
				.memberId(Id.generateNextId())
				.createdAt(now)
				.modifiedAt(now.plusHours(3))
				.title(new Title("dTitle"))
				.isDeleted(true)
				.build());

		categoryRepository.saveAllAndFlush(categories);
	}

	@Test
	@DisplayName("title 설정시 해당 글자로 시작하는 문자의 데이터를 가져온다")
	void findWhereStartsWithStringTest() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.startsWithTitle("a")
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();
		Title target = new Title("atitle2");
		assertThat(categories).extracting(Category::getTitle).contains(target);
	}

	@Test
	@DisplayName("생성일 시작 설정시 생성 시작일부터 그 이후 데이터만 가져온다")
	void findWhereStartCreatedAtTest() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.startCreatedAt(now)
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();
		assertThat(categories)
			.filteredOn(cate -> cate.getCreatedAt().isAfter(now) || cate.getCreatedAt().isEqual(now))
			.hasSize(3);
	}

	@Test
	@DisplayName("생성일 종료 설정시 생성일 종료일 이전 데이터만 가져온다")
	void findWhereEndCreatedAtTest() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.endCreatedAt(now)
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();
		assertThat(categories)
			.filteredOn(cate -> cate.getCreatedAt().isBefore(now) || cate.getCreatedAt().isEqual(now))
			.hasSize(0);
	}

	@Test
	@DisplayName("수정일 시작 설정시 수정 시작일부터 그 이후 데이터만 가져온다")
	void findWhereStartModifiedAtTest() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.startModifiedAt(now)
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(categories)
			.hasSize(3);
	}

	@Test
	@DisplayName("수정일 종료 설정시 수정 종료일 이전 데이터만 가져온다")
	void findWhereEndModifiedTest() {
		LocalDateTime time = LocalDateTime.now();
		Category toModify = categoryRepository.findById(toModifyId).orElseThrow();

		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.endModifiedAt(time)
			.build();

		toModify.changeTitle(new Title("aaa"));
		categoryRepository.save(toModify);

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(categories)
			.hasSize(2);
	}

	@Test
	@DisplayName("공유 데이터 설정시 공유 데이터만 가져와야 한다")
	void findWhereShareTure() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.eqShared(true)
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(categories)
			.filteredOn(Category::isShared)
			.hasSize(1);
	}

	@Test
	@DisplayName("삭제 데이터 설정시 삭제 데이터만 가져와야 한다")
	void findWhereDeleteTure() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.eqDeleted(true)
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(categories)
			.filteredOn(Category::isDeleted)
			.hasSize(1);
	}

	@Test
	@DisplayName("일치하는 회원 아이디 설정시 해당 회원 아이디만 조회해야 한다")
	void findWhereEqualMemberId() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.eqMemberId(myId)
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(categories)
			.filteredOn(cate -> cate.getMemberId().equals(myId))
			.hasSize(3);

	}

	@Test
	@DisplayName("2개 이상의 쿼리 옵션 가능 여부 테스트")
	void more2OptionTest() {
		CategorySearchCriteria criteria = CategorySearchCriteria.builder()
			.startsWithTitle("a")
			.eqShared(true)
			.build();

		List<Category> categories = queryFactory.selectFrom(category)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(categories)
			.hasSize(0);
	}
}