package com.logbasex.springbean.inject.configuration_annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);
		Person bean = applicationContext.getBean(Person.class);
		System.out.println(bean);
	}
}
