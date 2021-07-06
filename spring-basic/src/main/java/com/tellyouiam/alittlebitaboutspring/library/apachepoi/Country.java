package com.tellyouiam.alittlebitaboutspring.library.apachepoi;

//https://www.journaldev.com/2562/apache-poi-tutorial
public class Country {
	
	private String name;
	private String shortCode;
	
	public Country(){}
	
	public Country(String name, String shortCode) {
		this.name = name;
		this.shortCode = shortCode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShortCode() {
		return shortCode;
	}
	
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	
	@Override
	public String toString() {
		return "Country{" +
				"name='" + name + '\'' +
				", shortCode='" + shortCode + '\'' +
				'}';
	}
	
}
