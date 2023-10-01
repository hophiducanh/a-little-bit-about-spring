package com.logbasex.spring2dot7benchmark.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	private static final Logger log = LoggerFactory.getLogger(TestController.class);
	
	@GetMapping("/test")
	public void test() {
		log.info("Start to sleep {}", Thread.currentThread());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		log.info("Finished to sleep {}", Thread.currentThread());
	}
}
