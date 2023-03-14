package com.logbasex.springbean.lifecycle.example1;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("5. BeanPostProcessor#postProcessBeforeInitialization method");
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("9. BeanPostProcessor#postProcessAfterInitialization method");
		return bean;
	}
	
}
