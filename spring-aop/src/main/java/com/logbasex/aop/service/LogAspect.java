package com.logbasex.aop.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

//if we don't create this class, spring do not generate proxy.
@Aspect
@Component
@EnableAspectJAutoProxy
public class LogAspect {
	private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
	
	@Before("execution(* com.logbasex.aop.service.UserServiceImpl.*(..))")
	public void before(JoinPoint jp) {
		log.info("jp.getSignature().getName() = {}", jp.getSignature().getName());
	}
}
