package com.logbasex;

import javax.validation.constraints.NotNull;

@NameAndLanguage
public class Book {
	@NotNull
	private String name;
//	@Language
	private String language;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
}
