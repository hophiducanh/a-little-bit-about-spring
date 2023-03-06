package com.logbasex.springbean.inject.component_annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.logbasex.springbean.inject.component_annotation.*")
public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Main.class);
		Person bean = applicationContext.getBean(Person.class);
		System.out.println(bean);
	}
}
