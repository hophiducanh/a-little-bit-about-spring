package com.logbasex;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

/**
 * {@link <a href="https://www.logicbig.com/tutorials/spring-framework/spring-core/creating-custom-validation-constraints.html">...</a>}
 */
@Configuration
@ComponentScan
public class AppConfig {
	
	@Bean
	public Validator validatorFactory() {
		return new LocalValidatorFactoryBean();
	}
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		ClientBean bean = context.getBean(ClientBean.class);
		bean.run();
	}
}