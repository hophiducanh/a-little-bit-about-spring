package org.logbasex.service.import_selector_annotation.example1;

import org.logbasex.SpringBeanInjectApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <a href="https://www.logicbig.com/tutorials/spring-framework/spring-core/import-selector.html">...</a>
 */
@SpringBootApplication
public class Main {
	
	public static void main(String[] args) {
		System.setProperty("myProp", "1");
	
		// if using SpringApplication.run() will throw an Exception:
		// A bean with that name has already been defined
		// Because this is the difference between ConfigurableApplicationContext vs ApplicationContext.
		// https://stackoverflow.com/questions/30861709/configurableapplicationcontext-vs-applicationcontext
		// https://stackoverflow.com/questions/51008841/when-to-use-configurable-application-context-over-application-context
		
		// ApplicationContext gives you more of get/read only methods and encapsulated or doesn't allow Configuration
		// and lifecycle methods.
		// If you want more control over Life Cycle like Initialisation and Destruction, you can use
		// ConfigurableApplicationContext.
		
		// BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
		// if (!isAllowBeanDefinitionOverriding()) {
		//				throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
		//			}
//		ConfigurableApplicationContext run = SpringApplication.run(Main.class, args);
//		ClientBean bean0 = run.getBean(ClientBean.class);
//		System.out.println(bean0.doSomething());
		
		// ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		// isAllowBeanDefinitionOverriding() = true
		ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		ClientBean bean = context.getBean(ClientBean.class);
		System.out.println(bean.doSomething());
	}
}
