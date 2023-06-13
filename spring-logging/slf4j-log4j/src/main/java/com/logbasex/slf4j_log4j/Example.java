package com.logbasex.slf4j_log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Example {
	private static final Logger logger = LoggerFactory.getLogger(Example.class);
	
	public void doSomething() {
		logger.info("This is an info message");
	}
}
