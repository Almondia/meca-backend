package com.almondia.meca.category.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.cardhistory.domain.entity.CardHistory;
import com.almondia.meca.cardhistory.domain.repository.CardHistoryRepository;
import com.almondia.meca.cardhistory.domain.vo.Answer;
import com.almondia.meca.cardhistory.domain.vo.Score;
import com.almondia.meca.category.controller.dto.CategoryResponseDto;
import com.almondia.meca.category.controller.dto.CategoryWithHistoryResponseDto;
import com.almondia.meca.category.domain.entity.Category;
import com.almondia.meca.category.domain.repository.CategoryRepository;
import com.almondia.meca.category.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.controller.dto.CursorPage;
import com.almondia.meca.common.controller.dto.OffsetPage;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;

/**
 * 1. 페이징 형태로 잘 출력이 되는지 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfiguration.class)
class CategoryQueryDslRepositoryImplTest {

	Id memberId = Id.generateNextId();
	List<Id> categoryIds;
	List<Id> cardIds;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	CardHistoryRepository cardHistoryRepository;

	@Autowired
	CardRepository cardRepository;

	@BeforeEach
	void before() {
		initIds();
		inputCategoryData();
		inputCardData();
		inputCardHistoryData();
	}

	@Test
	void test() {
		OffsetPage<CategoryResponseDto> page = categoryRepository.findCategories(
			1,
			2,
			CategorySearchCriteria.builder().build(),
			SortOption.of(CategorySortField.TITLE, SortOrder.ASC));
		assertThat(page).hasFieldOrPropertyWithValue("pageNumber", 0)
			.hasFieldOrPropertyWithValue("totalPages", 1)
			.hasFieldOrPropertyWithValue("pageSize", 2)
			.hasFieldOrPropertyWithValue("totalElements", 4);
	}

	@Test
	void findCategoryWithStatisticsByMemberIdTest() {
		CursorPage<CategoryWithHistoryResponseDto> cursorPage = categoryRepository.findCategoryWithStatisticsByMemberId(
			4,
			memberId,
			null
		);
		assertThat(cursorPage)
			.hasFieldOrProperty("hasNext")
			.hasFieldOrProperty("pageSize")
			.hasFieldOrProperty("sortOrder");
	}

	private void inputCategoryData() {
		List<Category> categories = List.of(
			Category.builder()
				.categoryId(categoryIds.get(0))
				.memberId(memberId)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.title(new Title("btitle1")).build(),
			Category.builder()
				.categoryId(categoryIds.get(1))
				.memberId(memberId)
				.createdAt(LocalDateTime.now().plusHours(4))
				.modifiedAt(LocalDateTime.now().plusHours(4))
				.title(new Title("atitle2")).build(),
			Category.builder()
				.categoryId(categoryIds.get(2))
				.memberId(memberId)
				.createdAt(LocalDateTime.now().plusHours(2))
				.modifiedAt(LocalDateTime.now().plusHours(2))
				.title(new Title("ctitle")).build(),
			Category.builder()
				.categoryId(categoryIds.get(3))
				.memberId(memberId)
				.createdAt(LocalDateTime.now().plusHours(3))
				.modifiedAt(LocalDateTime.now().plusHours(3))
				.title(new Title("dTitle"))
				.isShared(true).build());

		categoryRepository.saveAllAndFlush(categories);
	}

	private void inputCardData() {
		List<Card> cards = List.of(
			OxCard.builder()
				.cardId(cardIds.get(0))
				.title(new com.almondia.meca.card.domain.vo.Title("title"))
				.categoryId(categoryIds.get(0))
				.cardType(CardType.OX_QUIZ)
				.isDeleted(false)
				.memberId(memberId)
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.build(),
			OxCard.builder()
				.cardId(cardIds.get(1))
				.title(new com.almondia.meca.card.domain.vo.Title("title"))
				.categoryId(categoryIds.get(0))
				.cardType(CardType.OX_QUIZ)
				.isDeleted(false)
				.memberId(memberId)
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.build(),
			OxCard.builder()
				.cardId(cardIds.get(2))
				.title(new com.almondia.meca.card.domain.vo.Title("title"))
				.categoryId(categoryIds.get(1))
				.cardType(CardType.OX_QUIZ)
				.isDeleted(false)
				.memberId(memberId)
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.build(),
			OxCard.builder()
				.cardId(cardIds.get(3))
				.title(new com.almondia.meca.card.domain.vo.Title("title"))
				.categoryId(categoryIds.get(1))
				.cardType(CardType.OX_QUIZ)
				.isDeleted(false)
				.memberId(memberId)
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.build(),
			OxCard.builder()
				.cardId(cardIds.get(4))
				.title(new com.almondia.meca.card.domain.vo.Title("title"))
				.categoryId(categoryIds.get(2))
				.cardType(CardType.OX_QUIZ)
				.isDeleted(false)
				.memberId(memberId)
				.question(new Question("question"))
				.oxAnswer(OxAnswer.O)
				.build()
		);
		cardRepository.saveAll(cards);
	}

	private void inputCardHistoryData() {
		List<CardHistory> cardHistories = List.of(
			CardHistory.builder()
				.cardHistoryId(Id.generateNextId())
				.cardId(cardIds.get(0))
				.score(new Score(100))
				.categoryId(categoryIds.get(0))
				.userAnswer(new Answer("O"))
				.build(),
			CardHistory.builder()
				.cardHistoryId(Id.generateNextId())
				.cardId(cardIds.get(0))
				.score(new Score(50))
				.userAnswer(new Answer("O"))
				.categoryId(categoryIds.get(0))
				.build(),
			CardHistory.builder()
				.cardHistoryId(Id.generateNextId())
				.cardId(cardIds.get(0))
				.score(new Score(0))
				.userAnswer(new Answer("O"))
				.categoryId(categoryIds.get(0))
				.build(),
			CardHistory.builder()
				.cardHistoryId(Id.generateNextId())
				.cardId(cardIds.get(1))
				.score(new Score(50))
				.userAnswer(new Answer("O"))
				.categoryId(categoryIds.get(0))
				.build(),
			CardHistory.builder()
				.cardHistoryId(Id.generateNextId())
				.cardId(cardIds.get(2))
				.score(new Score(20))
				.userAnswer(new Answer("O"))
				.categoryId(categoryIds.get(1))
				.build()
		);
		cardHistoryRepository.saveAll(cardHistories);
	}

	private void initIds() {
		categoryIds = new ArrayList<>();
		cardIds = new ArrayList<>();
		for (int i = 0; i < 4; ++i) {
			categoryIds.add(Id.generateNextId());
		}
		for (int i = 0; i < 5; ++i) {
			cardIds.add(Id.generateNextId());
		}
	}

}