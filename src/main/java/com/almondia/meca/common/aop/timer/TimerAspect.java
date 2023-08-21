package com.almondia.meca.common.aop.timer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class TimerAspect {

	// 모든 서비스 메서드에 적용 가능한 pointcut
	@Pointcut("execution(* com.almondia.meca..*Controller.*(..))")
	private void cut() {
	}

	@Pointcut("@annotation(com.almondia.meca.common.aop.timer.Timer)")
	private void timer() {
	}

	@Around("cut() || timer()")
	public Object calculateExecutionTime(ProceedingJoinPoint jointPoint) throws Throwable {
		// before
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// proceed
		Object result = jointPoint.proceed();

		// after
		stopWatch.stop();

		String methodName = jointPoint.getSignature().getName();
		log.info("Method name: " + methodName + ", " + "execution time: " + stopWatch.getTotalTimeMillis() + "[ms]");
		return result;
	}

}
