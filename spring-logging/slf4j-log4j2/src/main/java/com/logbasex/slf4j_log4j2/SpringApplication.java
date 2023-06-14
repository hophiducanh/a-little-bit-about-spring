package com.logbasex.slf4j_log4j2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringApplication {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
		Example example = context.getBean(Example.class);
		example.doSomething();
		
		Example1 example1 = context.getBean(Example1.class);
		example1.doSomething();
	}
}
