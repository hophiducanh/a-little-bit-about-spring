package org.logbasex.service.configuration_annotation;

public class ConfigurationPerson {
	private String name = "Injected by @Configuration annotation.";
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
