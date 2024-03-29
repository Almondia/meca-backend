package com.almondia.meca.card.domain.service;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;

import com.almondia.meca.card.domain.entity.Card;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.CardTestHelper;

/**
 * 1. 카드Id가 본인 소유와 일치한다면 카드 반환
 * 2. 카드Id가 본인 소유가 아닌 경우 권한 에러
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CardChecker.class, QueryDslConfiguration.class})
class CardCheckerTest {

	@Autowired
	CardChecker cardChecker;

	@Autowired
	EntityManager em;

	@Test
	@DisplayName("카드Id가 본인 소유와 일치한다면 카드 반환")
	void shouldReturnCardWhenCardIdIsMineTest() {
		Id cardId = Id.generateNextId();
		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		OxCard oxCard = CardTestHelper.genOxCard(memberId, categoryId, cardId);
		em.persist(oxCard);
		Card card = cardChecker.checkAuthority(cardId, memberId);
		assertThat(card).isInstanceOf(Card.class);
	}

	@Test
	@DisplayName("카드Id가 본인 소유가 아닌 경우 권한 에러")
	void shouldThrowAccessDeniedExceptionWhenNotMyCardRequestTest() {
		Id cardId = Id.generateNextId();
		Id categoryId = Id.generateNextId();
		Id memberId = Id.generateNextId();
		OxCard oxCard = CardTestHelper.genOxCard(memberId, categoryId, cardId);
		em.persist(oxCard);
		assertThatThrownBy(
			() -> cardChecker.checkAuthority(cardId, Id.generateNextId())).isInstanceOf(
			AccessDeniedException.class);
	}
}