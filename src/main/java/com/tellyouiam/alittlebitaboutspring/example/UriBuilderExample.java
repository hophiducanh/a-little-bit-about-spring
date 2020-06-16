package com.tellyouiam.alittlebitaboutspring.example;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UriBuilderExample {
	public static void main(String[] args) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("http")
				.host("www.baeldung.com")
				.path("/junit-5")
				.build();
		System.out.println(uriComponents.toUriString());
		
		String[] abc = Arrays.asList("a", "b").toArray(new String[1]);
		System.out.println(abc);
	
		String test = "Lobgasex loga nepe co so x";
		List<Character> chars = Pattern.compile("").splitAsStream(test).map(i -> i.charAt(0)).collect(Collectors.toList());
		System.out.println(chars);
	}
	
}
