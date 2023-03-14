package com.logbasex.springbean.lifecycle.example1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
	
	@Value("logbasex")
	private String name;
	
	@Bean
	public MyBeanPostProcessor myBeanPostProcessor() {
		return new MyBeanPostProcessor();
	}
	
	@Bean(initMethod = "init", destroyMethod = "myDestroy")
	public PersonBean personBean() {
		PersonBean personBean = new PersonBean();
		personBean.setId(123456);
		personBean.setName(name);
		return personBean;
	}
}
