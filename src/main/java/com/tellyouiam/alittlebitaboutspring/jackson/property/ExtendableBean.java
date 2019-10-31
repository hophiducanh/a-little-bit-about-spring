package com.tellyouiam.alittlebitaboutspring.jackson.property;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * @author : Ho Anh
 * @since : 30/08/2019, Fri
 **/
public class ExtendableBean {
	public String name;
	private Map<String, String> properties;

	public ExtendableBean(String my_bean) {
		this.name = my_bean;
	}

	@JsonIgnore
	public Map<String, String> getProperties() {
		return properties;
	}

	public void add(String attr1, String val1) {
		properties.put(attr1,val1);
	}
}
