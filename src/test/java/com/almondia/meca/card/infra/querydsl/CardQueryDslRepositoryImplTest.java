package com.almondia.meca.card.infra.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.repository.CardRepository;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 1. findCardByCategoryIdUsingCursorPaging lastId가 없다면 그냥 처음부터 limit 까지 조회
 * 2. findCardByCategoryIdUsingCursorPaging lastId가 있다면 lastId부터 limit까지 조회
 * 3. findMapByListOfCardIdAndMemberId시 element는 가져온 카드 쿼리가 같아야 한다
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class CardQueryDslRepositoryImplTest {

	@Autowired
	JPAQueryFactory queryFactory;

	@Autowired
	CardRepository cardRepository;

	Id categoryId = Id.generateNextId();
	Id memberId = Id.generateNextId();

	@BeforeEach
	void before() {
		List<Card> cards = List.of(
			OxCard.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title"))
				.cardType(CardType.OX_QUIZ)
				.question(new Question("question"))
				.categoryId(categoryId)
				.memberId(memberId)
				.oxAnswer(OxAnswer.O)
				.build(),
			OxCard.builder()
				.cardId(Id.generateNextId())
				.title(new Title("title2"))
				.cardType(CardType.OX_QUIZ)
				.question(new Question("question2"))
				.categoryId(categoryId)
				.memberId(memberId)
				.oxAnswer(OxAnswer.X)
				.build(),
			KeywordCard.builder()
				.cardId(Id.generateNextId())
				.title(new Title("key1"))
				.cardType(CardType.KEYWORD)
				.question(new Question("question key1"))
				.categoryId(categoryId)
				.memberId(memberId)
				.keywordAnswer(new KeywordAnswer("keyword"))
				.build(),
			KeywordCard.builder()
				.cardId(Id.generateNextId())
				.title(new Title("key1"))
				.cardType(CardType.KEYWORD)
				.question(new Question("question key1"))
				.categoryId(categoryId)
				.memberId(memberId)
				.keywordAnswer(new KeywordAnswer("keyword"))
				.build(),
			MultiChoiceCard.builder()
				.cardId(Id.generateNextId())
				.title(new Title("multi1"))
				.cardType(CardType.MULTI_CHOICE)
				.question(new Question("question key1"))
				.categoryId(categoryId)
				.memberId(memberId)
				.multiChoiceAnswer(new MultiChoiceAnswer(1))
				.build());

		cardRepository.saveAll(cards);
	}

	@Test
	@DisplayName("findCardByCategoryIdUsingCursorPaging lastId가 없다면 그냥 처음부터 limit 까지 조회")
	void shouldReturnFirstToLimitSizeWhenNoInputLastIdTest() {
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.build();
		List<Card> paging = cardRepository.findCardByCategoryIdUsingCursorPaging(2, criteria,
			SortOption.of(CardSortField.CREATED_AT,
				SortOrder.DESC));
		assertThat(paging).hasSize(2);
	}

	@Test
	@DisplayName("findCardByCategoryIdUsingCursorPaging lastId가 있다면 lastId부터 limit까지 조회")
	void shouldReturnIndexToLimitSizeWhenNoInputLastIdTest() {
		List<Card> all = cardRepository.findAll();
		all.sort(Comparator.comparing(Card::getCreatedAt, Comparator.reverseOrder()));
		LocalDateTime time = all.get(0).getCreatedAt();
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.eqMemberId(memberId)
			.endCreatedAt(time)
			.build();
		List<Card> paging = cardRepository.findCardByCategoryIdUsingCursorPaging(2, criteria,
			SortOption.of(CardSortField.CREATED_AT,
				SortOrder.DESC));
		assertThat(paging.get(0).getCreatedAt()).isEqualTo(time);
		assertThat(paging).hasSize(2);
	}

	@Test
	@DisplayName("findMapByListOfCardIdAndMemberId시 element는 가져온 카드 쿼리가 같아야 한다")
	void shouldReturnMapWhenCallFindCardByListOfCardIdTest() {
		List<Card> cards = cardRepository.findAll();
		List<Id> cardIds = List.of(cards.get(0).getCardId(), cards.get(1).getCardId(), cards.get(2).getCardId());
		Map<Id, List<Id>> fetch = cardRepository.findMapByListOfCardIdAndMemberId(cardIds, memberId);
		long sum = fetch.values()
			.stream()
			.mapToLong(Collection::size)
			.sum();
		assertThat(sum).isEqualTo(3);
	}
}