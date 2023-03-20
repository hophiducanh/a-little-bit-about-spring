package com.logbasex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {
//				"com.logbasex.controller",
				"com.logbasex.service",
		}
)
public class SpringBean404Application {
	public static void main(String[] args) {
		SpringApplication.run(SpringBean404Application.class, args);
		System.out.println("Hello");
	}
}
