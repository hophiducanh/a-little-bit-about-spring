package com.logbasex.springbean.lifecycle.example1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <a href="https://medium.com/@wdn0612/spring-beans-from-born-to-death-d2a325d872d7">...</a>
 */
public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
		context.getBean(PersonBean.class);
		//deprecated
//		(context).destroy();
		(context).close();
	}
}
