package com.almondia.meca.card.infra.querydsl;

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

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.entity.QCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.card.repository.CardRepository;
import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 1. title 설정시 해당 글자로 시작하는 문자의 카드 데이터를 가져온다
 * 2. 생성일 시작 설정시 생성 시작일부터 그 이후 데이터만 가져온다
 * 3. 생성일 종료 설정시 생성일 종료일 이전 데이터만 가져온다
 * 4. 수정일 종료 설정시 수정 종료일 이전 데이터만 가져온다
 * 5. 삭제 데이터 설정시 삭제 데이터만 가져와야 한다
 * 6. cardId 설정시 해당 CardId인 데이터만 가져와야 한다
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfiguration.class, JpaAuditingConfiguration.class})
class CardSearchCriteriaTest {

	@Autowired
	JPAQueryFactory queryFactory;

	@Autowired
	CardRepository cardRepository;

	QCard card = QCard.card;
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
	@DisplayName("title 설정시 해당 글자로 시작하는 문자의 데이터를 가져온다")
	void findWhereStartsWithStringTest() {
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.startsWithTitle("title2")
			.build();
		List<Card> cards = queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.fetch();
		assertThat(cards).extracting(Card::getTitle).contains(new Title("title2"));
		assertThat(cards.get(0)).isInstanceOf(OxCard.class);
		assertThat(cards.get(0))
			.hasFieldOrPropertyWithValue("oxAnswer", OxAnswer.X);
	}

	@Test
	@DisplayName("생성일 시작 설정시 생성 시작일부터 그 이후 데이터만 가져온다")
	void findWhereStartCreatedAtTest() {
		LocalDateTime now = LocalDateTime.now();
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.startCreatedAt(now.minusHours(2L))
			.build();

		List<Card> cards = queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.fetch();
		assertThat(cards)
			.hasSize(5);
	}

	@Test
	@DisplayName("생성일 종료 설정시 생성일 종료일 이전 데이터만 가져온다")
	void findWhereEndCreatedAtTest() {
		LocalDateTime now = LocalDateTime.now().plusDays(1L);
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.endCreatedAt(now)
			.build();

		List<Card> cards = queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.fetch();
		assertThat(cards)
			.hasSize(5);
	}

	@Test
	@DisplayName("수정일 종료 설정시 수정 종료일 이전 데이터만 가져온다")
	void findWhereEndModifiedTest() {
		LocalDateTime time = LocalDateTime.now();
		Card toModify = cardRepository.findAll().get(0);

		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.endModifiedAt(time)
			.build();

		toModify.changeTitle(new Title("titl2e"));
		cardRepository.save(toModify);

		List<Card> categories = queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(categories)
			.hasSize(4);
	}

	@Test
	@DisplayName("삭제 데이터 설정시 삭제 데이터만 가져와야 한다")
	void findWhereDeleteTure() {
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.eqDeleted(true)
			.build();
		System.out.println(criteria.getPredicate());
		List<Card> cards = queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(cards)
			.hasSize(0);
	}

	@Test
	@DisplayName("2개 이상의 쿼리 옵션 가능 여부 테스트")
	void more2OptionTest() {
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.startsWithTitle("a")
			.eqDeleted(true)
			.build();

		List<Card> cards = queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(cards)
			.hasSize(0);
	}

	@Test
	@DisplayName("cardId 설정시 해당 CardId인 데이터만 가져와야 한다")
	void findWhereCardIdTest() {
		List<Card> all = cardRepository.findAll();
		CardSearchCriteria criteria = CardSearchCriteria.builder()
			.eqCardId(all.get(0).getCardId())
			.build();

		System.out.println(criteria.getPredicate());
		List<Card> cards = queryFactory.selectFrom(card)
			.where(criteria.getPredicate())
			.fetch();

		assertThat(cards)
			.hasSize(1);
	}
}