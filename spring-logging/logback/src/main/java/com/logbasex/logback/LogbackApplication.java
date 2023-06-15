package com.logbasex.logback;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LogbackApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(LogbackApplication.class, args);
		Logback example = context.getBean(Logback.class);
		example.doSomething();
	}
}
