package com.almondia.meca.common.aop.timer;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.almondia.meca.category.controller.CategoryController;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@SpringBootTest
class TimerAspectTest {

	@Autowired
	CategoryController categoryController;

	@Autowired
	TimerAspect timerAspect;

	private Logger logger;

	private ListAppender<ILoggingEvent> listAppender;

	@BeforeEach
	void setUp() {
		logger = (Logger)LoggerFactory.getLogger(TimerAspect.class);

		listAppender = new ListAppender<>();
		listAppender.start();
		logger.addAppender(listAppender);
	}

	@AfterEach
	void tearDown() {
		logger.detachAppender(listAppender);
	}

	@Test
	@DisplayName("컨트롤러 pointCut 테스트")
	void shouldDoWhenExecuteServiceClassMethodTest() {
		categoryController.getCursorPagingCategoryShare(null, 0, null);
		assertThat(listAppender.list).isNotEmpty().hasSizeGreaterThan(0);
	}
}