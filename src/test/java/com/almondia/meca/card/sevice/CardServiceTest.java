package com.almondia.meca.card.sevice;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.card.controller.dto.SaveCardRequestDto;
import com.almondia.meca.card.domain.entity.KeywordCard;
import com.almondia.meca.card.domain.entity.MultiChoiceCard;
import com.almondia.meca.card.domain.entity.OxCard;
import com.almondia.meca.card.domain.vo.CardType;
import com.almondia.meca.card.domain.vo.KeywordAnswer;
import com.almondia.meca.card.domain.vo.MultiChoiceAnswer;
import com.almondia.meca.card.domain.vo.OxAnswer;
import com.almondia.meca.card.domain.vo.Question;
import com.almondia.meca.card.domain.vo.Title;
import com.almondia.meca.card.repository.KeywordCardRepository;
import com.almondia.meca.card.repository.MultiChoiceCardRepository;
import com.almondia.meca.card.repository.OxCardRepository;
import com.almondia.meca.card.sevice.checker.CardChecker;
import com.almondia.meca.category.service.checker.CategoryChecker;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CardService.class, QueryDslConfiguration.class, CategoryChecker.class, CardChecker.class})
class CardServiceTest {

	@Autowired
	CardService cardService;

	@Autowired
	OxCardRepository oxCardRepository;

	@Autowired
	KeywordCardRepository keywordCardRepository;

	@Autowired
	MultiChoiceCardRepository multiChoiceCardRepository;

	/**
	 * 1. oxCard type정보가 들어가면 oxCard 정보가 저장되는지 검증
	 * 2. keywordCard type 정보가 들어가면 keywordCard 정보가 저장되는지 검증
	 * 3. multi choice card type 정보가 들어가면 MultiChoiceCard 정보가 저장되는 지 검증
	 */
	@Nested
	@DisplayName("카드 저장 테스트")
	class SaveCardTest {
		@Test
		@DisplayName("oxCard type정보가 들어가면 oxCard 정보가 저장되는지 검증")
		void shouldSaveOxCardTest() {
			cardService.saveCard(makeSaveCardRequest().oxAnswer(OxAnswer.O).cardType(CardType.OX_QUIZ).build(),
				Id.generateNextId());
			List<OxCard> all = oxCardRepository.findAll();
			assertThat(all).isNotEmpty();
		}

		@Test
		@DisplayName("keywordCard type 정보가 들어가면 keywordCard 정보가 저장되는지 검증")
		void shouldSaveKeywordCardTest() {
			cardService.saveCard(makeSaveCardRequest()
				.keywordAnswer(new KeywordAnswer("asdf"))
				.cardType(CardType.KEYWORD).build(), Id.generateNextId());
			List<KeywordCard> all = keywordCardRepository.findAll();
			assertThat(all).isNotEmpty();
		}

		@Test
		@DisplayName("multi choice card type 정보가 들어가면 MultiChoiceCard 정보가 저장되는 지 검증")
		void shouldSaveMultiCardTest() {
			cardService.saveCard(
				makeSaveCardRequest().multiChoiceAnswer(new MultiChoiceAnswer(1))
					.cardType(CardType.MULTI_CHOICE).build(),
				Id.generateNextId());
			List<MultiChoiceCard> all = multiChoiceCardRepository.findAll();
			assertThat(all).isNotEmpty();
		}

		private SaveCardRequestDto.SaveCardRequestDtoBuilder makeSaveCardRequest() {
			return SaveCardRequestDto.builder()
				.title(new Title("title"))
				.question(new Question("question"))
				.categoryId(Id.generateNextId())
				.images("A,B,C");
		}
	}

}