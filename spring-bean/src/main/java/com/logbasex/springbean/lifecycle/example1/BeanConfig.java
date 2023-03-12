package com.logbasex.springbean.lifecycle.example1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
	@Bean
	public MyBeanPostProcessor myBeanPostProcessor() {
		return new MyBeanPostProcessor();
	}
	
	@Bean(initMethod = "init", destroyMethod = "myDestroy")
	public PersonBean personBean() {
		PersonBean personBean = new PersonBean();
		personBean.setId(123456);
		personBean.setName("lobgasex");
		return personBean;
	}
}
