package com.tellyouiam.alittlebitaboutspring.utils.beanutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Ho Anh
 * @since : 08/10/2019, Tue
 **/
public class Course {
	private String name;
	private List<String> codes;
	private Map<String, String> enrolledStudent = new HashMap<>();

	public Course() {

	}

	public Course(String name, List<String> codes, Map<String, String> enrolledStudent) {
		this.name = name;
		this.codes = codes;
		this.enrolledStudent = enrolledStudent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public Map<String, String> getEnrolledStudent() {
		return enrolledStudent;
	}

	public void setEnrolledStudent(Map<String, String> enrolledStudent) {
		this.enrolledStudent = enrolledStudent;
	}
}
