package com.logbasex.slf4j_log4j2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
		Example bean = context.getBean(Example.class);
		bean.doSomething();
	}
}
