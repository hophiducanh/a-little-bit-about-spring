package org.logbasex.example1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
		context.getBean(PersonBean.class);
		//deprecated
//		(context).destroy();
		(context).close();
	}
}
