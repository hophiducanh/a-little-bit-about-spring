package com.tellyouiam.alittlebitaboutspring.beanutils;

/**
 * @author : Ho Anh
 * @since : 08/10/2019, Tue
 **/
public class Student {
	private String id;
	private String name;

	public Student() {

	}

	public Student(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Student{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			'}';
	}
}
