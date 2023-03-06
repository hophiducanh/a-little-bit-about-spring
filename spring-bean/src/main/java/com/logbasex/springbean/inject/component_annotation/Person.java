package com.logbasex.springbean.inject.component_annotation;

import org.springframework.stereotype.Component;

@Component
public class Person {
	private String name = "Logbasex";
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
