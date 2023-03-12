package com.logbasex.springbean.lifecycle.example1;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;


public class PersonBean implements InitializingBean, BeanFactoryAware, BeanNameAware, DisposableBean {
	
	private Integer id;
	private String name;
	
	public PersonBean(){
		System.out.println("1. I am alive!");
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		System.out.println("2. Populate attribute: my name is " + name);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("4. Invoking BeanFactoryAware#setBeanFactory method");
	}
	
	@Override
	public void setBeanName(String s) {
		System.out.println("3. Invoking BeanNameAware#setBeanName method");
	}
	
	public void init(){
		System.out.println("7. Customized init method");
	}
	
	public void myDestroy(){
		System.out.println("10. Customized destroy method");
	}
	
	@Override
	public void destroy() throws Exception {
		System.out.println("9. DisposableBean#destroy method");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("6. InitializingBea#afterPropertiesSet method");
	}
}
