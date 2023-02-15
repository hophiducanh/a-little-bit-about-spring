package com.logbasex.async_annotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SpringAsyncAnnotationApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringAsyncAnnotationApplication.class, args);
	}
}
