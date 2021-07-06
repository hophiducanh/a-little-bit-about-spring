package com.tellyouiam.alittlebitaboutspring.utils.beanutils;

import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author : Ho Anh
 * @since : 08/10/2019, Tue
 **/
public class Main {
	public static void main(String[] args) {
		//BeanUtils don't copy field doesn't map thus no error throw
		Course course = new Course("anh", Arrays.asList("a", "b"),
			new HashMap<String, String>() {{
				put("x", "y");
				put("c", "d");
			}});

		//Java 9
//		Map<String, String> test = Map.of(
//			"a", "b",
//			"c", "d"
//		);

		//Guava library
//		Map<String, String> test = ImmutableMap.<String, String>builder()
//			.put("k1", "v1")
//			.put("k2", "v2").build();

		Student student = new Student("5", "lan");
		BeanUtils.copyProperties(course, student);
		System.out.println(student.toString());
	}
}
