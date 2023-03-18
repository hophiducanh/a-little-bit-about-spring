package org.logbasex.service.component_annotation;

import org.springframework.stereotype.Component;

@Component
public class ComponentPerson {
	private String name = "Injected by @Component annotation.";
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
