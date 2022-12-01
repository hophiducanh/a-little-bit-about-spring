package com.logbasex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ClientBean {
	@Autowired
	private Validator validator;
	
	public void run() {
		Book book = new Book();
		book.setName("Alien Explorer");
		book.setLanguage("Englis");
		
		Set<ConstraintViolation<Book>> c = validator.validate(book);
		if (c.size() > 0) {
			System.err.println("validation errors:");
			c.stream().map(ConstraintViolation::getMessage).forEach(System.err::println);
		}
	}
}
