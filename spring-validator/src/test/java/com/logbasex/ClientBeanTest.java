package com.logbasex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientBean.class)
@ContextConfiguration(classes = {LocalValidatorFactoryBean.class})
class ClientBeanTest {
	
	@Autowired
	private Validator validator;
	
	@Test
	void run() {
		Book book = new Book();
		book.setName("Alien Explorer");
		book.setLanguage("Englis");
		
		Set<ConstraintViolation<Book>> c = validator.validate(book);
		Assertions.assertEquals(1, c.size());
	}
}