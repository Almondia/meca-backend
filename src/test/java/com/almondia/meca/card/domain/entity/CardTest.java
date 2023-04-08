package com.almondia.meca.card.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

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

import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.Description;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;

/**
 * 1. 데이터 속성 생성 테스트
 * 2. entity를 생성해서 저장시 createdAt과 modifiedAt이 자동으로 업데이트되며 서로 같음
 * 3. entity 수정시 modifiedAt이 업데이트되며 modifiedAt이 createdAt보다 이후의 날짜여야 함
 * 4. delete, rollback 메서드 사용시 isDeleted 상태가 변경되어야 함
 * 5. title 변경 메서드  정상 동작 테스트
 * 6. images 변경 메서드 정상 동작 테스트
 * 7. question 변경 메서드 정상 동작 테스트
 * 8. categoryId 변경 메서드 정상 동작 테스트
 * 9. changeEditText 동작 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class CardTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("데이터 속성 생성 테스트")
	void oxCardAttributeCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(Card.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("Card");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("description", "question", "memberId", "isDeleted", "cardId", "categoryId",
				"title",
				"images",
				"createdAt", "modifiedAt");
	}

	@Test
	@DisplayName("entity를 생성해서 저장시 createdAt과 modifiedAt이 자동으로 업데이트되며 서로 같음")
	void shouldUpdateCreatedAtAndModifiedAtAndTheyAreEqualWhenEntitySave() {
		JpaRepository<OxCard, Id> oxCardJpaRepository = new SimpleJpaRepository<>(OxCard.class, entityManager);
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.question(new Question("Question"))
			.categoryId(Id.generateNextId())
			.memberId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.build();
		oxCardJpaRepository.save(oxCard);
		OxCard result = oxCardJpaRepository.findById(oxCard.getCardId()).orElseThrow();
		LocalDateTime createdAt = result.getCreatedAt();
		LocalDateTime modifiedAt = result.getModifiedAt();
		assertThat(createdAt).isEqualTo(modifiedAt);
	}

	@Test
	@DisplayName("entity 수정시 modifiedAt이 업데이트되며 modifiedAt이 createdAt보다 이후의 날짜여야 함")
	void shouldUpdateModifiedAtAndModifiedAtAfterThanCreatedAtWhenEntityUpdate() throws InterruptedException {
		JpaRepository<OxCard, Id> oxCardJpaRepository = new SimpleJpaRepository<>(OxCard.class, entityManager);
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.question(new Question("Question"))
			.memberId(Id.generateNextId())
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.build();
		System.out.println(oxCard);
		oxCardJpaRepository.save(oxCard);
		OxCard temp = oxCardJpaRepository.findById(oxCard.getCardId()).orElseThrow();
		temp.delete();
		oxCardJpaRepository.saveAndFlush(temp);
		Thread.sleep(100);
		OxCard result = oxCardJpaRepository.findById(oxCard.getCardId()).orElseThrow();
		assertThat(result.getModifiedAt()).isAfter(result.getCreatedAt());
	}

	@Test
	@DisplayName("delete 메서드시 isDeleted는 false가 true가 되야함")
	void shouldTrueWhenCallDelete() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.build();
		oxCard.delete();
		assertThat(oxCard.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("rollback 메서드시 isDeleted는 false -> true 가 되야함")
	void shouldFalseWhenCallRollback() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.isDeleted(true)
			.build();
		oxCard.rollback();
		assertThat(oxCard.isDeleted()).isFalse();
	}

	@Test
	@DisplayName("title 변경 메서드  정상 동작 테스트")
	void shouldChangeTitleWhenCallChangeTitleTest() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.isDeleted(true)
			.build();
		oxCard.changeTitle(new Title("hello"));
		assertThat(oxCard).hasFieldOrPropertyWithValue("title", new Title("hello"));
	}

	@Test
	@DisplayName("images 변경 메서드 정상 동작 테스트")
	void shouldChangeImagesWhenCallChangeImagesTest() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.isDeleted(true)
			.build();
		oxCard.changeImages("A,B,C,D");
		assertThat(oxCard.getImages()).contains(new Image("A"), new Image("B"), new Image("C"), new Image("D"));
	}

	@Test
	@DisplayName("question 변경 메서드 정상 동작 테스트")
	void shouldChangeQuestionWhenCallChangeQuestionTest() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.isDeleted(true)
			.build();
		oxCard.changeQuestion(new Question("question2"));
		assertThat(oxCard).hasFieldOrPropertyWithValue("question", new Question("question2"));
	}

	@Test
	@DisplayName("categoryId 변경 메서드 정상 동작 테스트")
	void shouldReturnCategoryIdWhenCallChangeCategoryIdTest() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.isDeleted(true)
			.build();
		Id categoryId = Id.generateNextId();
		oxCard.changeCategoryId(categoryId);
		assertThat(oxCard).hasFieldOrPropertyWithValue("categoryId", categoryId);
	}

	@Test
	@DisplayName("부모 엔티티인 카드 엔티티에서 하위 타입의 cardType이 예상대로 조회 되는지 테스트")
	void shouldReturnChildCardTypeWhenCallParentCardTypeTest() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.isDeleted(true)
			.build();
		KeywordCard keywordCard = KeywordCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.KEYWORD)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.keywordAnswer(new KeywordAnswer("keyword"))
			.isDeleted(true)
			.build();
		MultiChoiceCard multiChoiceCard = MultiChoiceCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.MULTI_CHOICE)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.multiChoiceAnswer(new MultiChoiceAnswer(1))
			.isDeleted(true)
			.build();
		assertThat(((Card)oxCard).getCardType()).isEqualTo(CardType.OX_QUIZ);
		assertThat(((Card)keywordCard).getCardType()).isEqualTo(CardType.KEYWORD);
		assertThat(((Card)multiChoiceCard).getCardType()).isEqualTo(CardType.MULTI_CHOICE);
	}

	@Test
	@DisplayName("changeEditText 메서드 정상 동작 테스트")
	void changeEditTextTest() {
		OxCard oxCard = OxCard.builder()
			.cardId(Id.generateNextId())
			.cardType(CardType.OX_QUIZ)
			.categoryId(Id.generateNextId())
			.images(List.of(new Image("image1"), new Image("image2")))
			.title(new Title("title"))
			.oxAnswer(OxAnswer.O)
			.isDeleted(true)
			.build();
		oxCard.changeEditText(new Description("hello"));
		assertThat(oxCard).hasFieldOrPropertyWithValue("description", new Description("hello"));
	}
}