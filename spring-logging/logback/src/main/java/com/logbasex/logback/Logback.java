package com.logbasex.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Logback {
	private static final Logger logger = LoggerFactory.getLogger(Logback.class);
	
	public void doSomething() {
		logger.info("This is an INFO message");
		logger.debug("This is an DEBUG message");
		logger.error("This is an ERROR message");
	}
}
