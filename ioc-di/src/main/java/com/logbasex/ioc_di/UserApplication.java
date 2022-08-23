package com.logbasex.ioc_di;

import com.logbasex.ioc_di.controller.UserController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class UserApplication {
	public static void main(String[] args) {
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(UserApplication.class);
//		UserController userController = (UserController) applicationContext.getBean("userController");
//		userController.sayHello();
		
		SpringApplication.run(UserApplication.class, args);
	}
}
