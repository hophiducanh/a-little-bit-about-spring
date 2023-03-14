package com.logbasex.springbean.lifecycle.example1;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

import javax.annotation.PostConstruct;


public class PersonBean implements InitializingBean, BeanFactoryAware, BeanNameAware, DisposableBean {
	
	@PostConstruct
	public void postConstruct() {
		// https://stackoverflow.com/questions/25528775/spring-init-method-postconstruct-afterpropertiesset-when-to-use-one-over-ot
		// The difference between using the constructor and the other options is that the constructor code is the
		// first to be executed, while the other options will be called only after dependencies were injected into the
		// bean (either from @Autowired annotations or the XML file).
		//
		//Code you write in the constructor will run while the bean's properties are still not initiated. All
		// @Autowired fields would be null. Sometimes this is what you want, but usually you want the code to run
		// after properties are set.
		//
		//Other than this, I do not see a difference, other than order of execution. I do not think there is a case
		// you would want to have all options in the same class.
		
		// chưa clear lắm cần xác thực.
		// Spring calls methods the annotated with @PostConstruct only once after the initialization of bean properties.
		// https://medium.com/javarevisited/how-about-postconstruct-40fe1297e70d
		System.out.println("6. Run post constructor.");
	}
	
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
		System.out.println("8. Customized init method");
	}
	
	public void myDestroy(){
		System.out.println("11. Customized destroy method");
	}
	
	@Override
	public void destroy() throws Exception {
		System.out.println("10. DisposableBean#destroy method");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//https://stackoverflow.com/questions/35765447/afterpropertiesset-method-in-spring-life-cycle
		//https://stackoverflow.com/questions/31528697/spring-boot-detect-and-terminate-if-property-not-set
		System.out.println("7. InitializingBea#afterPropertiesSet method");
	}
}
