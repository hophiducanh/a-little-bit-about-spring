package org.logbasex.service.import_annotation;

public class ImportPerson {
	private String name = "Injected by @Import annotation.";
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
