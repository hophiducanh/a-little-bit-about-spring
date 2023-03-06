package com.logbasex.springbean.inject.import_selector_annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

@Import(MyImportSelector.class)
public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Person.class);
		Person bean = applicationContext.getBean(Person.class);
		System.out.println(bean);
	}
}
