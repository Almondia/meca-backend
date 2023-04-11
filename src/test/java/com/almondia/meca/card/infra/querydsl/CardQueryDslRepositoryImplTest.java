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

import com.almondia.meca.card.controller.dto.SharedCardResponseDto;
import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.repository.CardRepository;
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
import com.almondia.meca.common.infra.querydsl.SortOption;
import com.almondia.meca.common.infra.querydsl.SortOrder;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.member.repository.MemberRepository;
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

	@Autowired
	MemberRepository memberRepository;

	Id categoryId = Id.generateNextId();
	Id memberId = Id.generateNextId();

	@BeforeEach
	void before() {
		List<Card> cards = List.of(OxCard.builder()
			.cardId(Id.generateNextId())
			.title(new Title("title"))
			.cardType(CardType.OX_QUIZ)
			.question(new Question("question"))
			.categoryId(categoryId)
			.memberId(memberId)
			.oxAnswer(OxAnswer.O)
			.build(), OxCard.builder()
			.cardId(Id.generateNextId())
			.title(new Title("title2"))
			.cardType(CardType.OX_QUIZ)
			.question(new Question("question2"))
			.categoryId(categoryId)
			.memberId(memberId)
			.oxAnswer(OxAnswer.X)
			.build(), KeywordCard.builder()
			.cardId(Id.generateNextId())
			.title(new Title("key1"))
			.cardType(CardType.KEYWORD)
			.question(new Question("question key1"))
			.categoryId(categoryId)
			.memberId(memberId)
			.keywordAnswer(new KeywordAnswer("keyword"))
			.build(), KeywordCard.builder()
			.cardId(Id.generateNextId())
			.title(new Title("key1"))
			.cardType(CardType.KEYWORD)
			.question(new Question("question key1"))
			.categoryId(categoryId)
			.memberId(memberId)
			.keywordAnswer(new KeywordAnswer("keyword"))
			.build(), MultiChoiceCard.builder()
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
		CardSearchCriteria criteria = CardSearchCriteria.builder().build();
		List<Card> paging = cardRepository.findCardByCategoryIdUsingCursorPaging(2, criteria,
			SortOption.of(CardSortField.CREATED_AT, SortOrder.DESC));
		assertThat(paging).hasSize(2);
	}

	@Test
	@DisplayName("findCardByCategoryIdUsingCursorPaging lastId가 있다면 lastId부터 limit까지 조회")
	void shouldReturnIndexToLimitSizeWhenNoInputLastIdTest() {
		List<Card> all = cardRepository.findAll();
		all.sort(Comparator.comparing(Card::getCreatedAt, Comparator.reverseOrder()));
		LocalDateTime time = all.get(0).getCreatedAt();
		CardSearchCriteria criteria = CardSearchCriteria.builder().eqMemberId(memberId).endCreatedAt(time).build();
		List<Card> paging = cardRepository.findCardByCategoryIdUsingCursorPaging(2, criteria,
			SortOption.of(CardSortField.CREATED_AT, SortOrder.DESC));
		assertThat(paging.get(0).getCreatedAt()).isEqualTo(time);
		assertThat(paging).hasSize(2);
	}

	@Test
	@DisplayName("findMapByListOfCardIdAndMemberId시 element는 가져온 카드 쿼리가 같아야 한다")
	void shouldReturnMapWhenCallFindCardByListOfCardIdTest() {
		List<Card> cards = cardRepository.findAll();
		List<Id> cardIds = List.of(cards.get(0).getCardId(), cards.get(1).getCardId(), cards.get(2).getCardId());
		Map<Id, List<Id>> fetch = cardRepository.findMapByListOfCardIdAndMemberId(cardIds, memberId);
		long sum = fetch.values().stream().mapToLong(Collection::size).sum();
		assertThat(sum).isEqualTo(3);
	}

	@Test
	@DisplayName("findCardsWithHistoryByCategoryIdScoreAsc")
	void test() {
		List<Card> cards = cardRepository.findCardByCategoryIdScoreAsc(categoryId, 5);
		assertThat(cards).hasSize(5);
	}

	@Test
	@DisplayName("countCardsByCategoryId 카드 총 조회")
	void shouldReturnCountWhenCallCountCardsByCategoryIdTest() {
		long count = cardRepository.countCardsByCategoryId(categoryId);
		assertThat(count).isEqualTo(5);
	}

	@Test
	@DisplayName("findSharedCard 공유 카드 단일 조회")
	void test2() {
		Id cardId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		memberRepository.save(Member.builder()
			.memberId(memberId)
			.email(new Email("email@naver.com"))
			.name(new Name("name"))
			.oAuthType(OAuthType.KAKAO)
			.oauthId("1234")
			.role(Role.USER)
			.build());

		cardRepository.save(OxCard.builder()
			.memberId(memberId)
			.categoryId(Id.generateNextId())
			.cardId(cardId)
			.oxAnswer(OxAnswer.O)
			.title(new Title("title"))
			.question(new Question("question"))
			.description(new Description("description"))
			.build());
		SharedCardResponseDto sharedCard = cardRepository.findSharedCard(cardId);
		assertThat(sharedCard.getCardInfo()).isNotNull();
		assertThat(sharedCard.getMemberInfo()).isNotNull();
	}

}