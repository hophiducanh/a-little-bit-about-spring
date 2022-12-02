package com.logbasex;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

// http://dolszewski.com/java/multiple-field-validation/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Book.class)
class BookTest {
	private Validator validator;
	
	//https://stackoverflow.com/questions/49161532/junit-null-pointer-exception-with-before
	@BeforeEach
	public void setUp() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}
	@Test
	public void shouldValidateBook() {
		Book book = new Book();
		book.setName("Alien Explorer");
		book.setLanguage("Englis");
		
		Set<ConstraintViolation<Book>> c = validator.validate(book);
		Assertions.assertEquals(1, c.size());
	}
	
}