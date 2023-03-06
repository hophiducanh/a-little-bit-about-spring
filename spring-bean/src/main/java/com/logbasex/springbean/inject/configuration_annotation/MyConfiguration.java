package com.logbasex.springbean.inject.configuration_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {
	@Bean
	public Person person() {
		Person person = new Person();
		person.setName("spring");
		return person;
	}
}
