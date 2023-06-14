package com.logbasex.slf4j_log4j2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Example1 {
	private static final Logger logger = LoggerFactory.getLogger(Example1.class);
	
	public void doSomething() {
		logger.info("Example1: This is an INFO message");
		logger.debug("Example1: This is an DEBUG message");
		logger.trace("Example1: This is an TRACE message");
		logger.warn("Example1: This is an WARN message");
		logger.error("Example1: This is an ERROR message");
	}
}
