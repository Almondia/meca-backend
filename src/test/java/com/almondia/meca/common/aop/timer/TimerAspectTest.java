package com.almondia.meca.common.aop.timer;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.application.MemberService;
import com.almondia.meca.member.domain.repository.MemberRepository;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@SpringBootTest
class TimerAspectTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;

	@Test
	@DisplayName("서비스 pointCut 테스트")
	void shouldDoWhenExecuteServiceClassMethodTest() {
		// given
		Id memberId = Id.generateNextId();
		memberRepository.save(MemberTestHelper.generateMember(memberId));
		// 로그 메시지 캡처 및 검증

		// when
		memberService.findMember(memberId);

		// then
		ListAppender<ILoggingEvent> appender = new ListAppender<>();
		appender.start();
		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		Logger targetLogger = loggerContext.getLogger(MemberService.class);
		targetLogger.addAppender(appender);
		List<ILoggingEvent> logEvents = appender.list;
		for (ILoggingEvent logEvent : logEvents) {
			if (logEvent.getFormattedMessage().contains("execution time")) {
				assertThat(logEvent.getLevel()).isEqualTo(ch.qos.logback.classic.Level.INFO);
			}
		}
	}

}