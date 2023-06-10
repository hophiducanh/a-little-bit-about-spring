package org.logbasex.service.import_selector_annotation.example2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.SpringVersion;

/**
 * <a href="https://www.logicbig.com/tutorials/spring-framework/spring-core/import-selector.html">...</a>
 */
@SpringBootApplication
public class Main2 {
	
	public static void main(String[] args) {
		System.out.println(SpringBootVersion.getVersion());
		
		// set isAllowBeanDefinitionOverriding() = false rồi mới khởi tạo.
		// check application.yml
		ConfigurableApplicationContext run = SpringApplication.run(Main2.class, args);
		AppBean2 configurableBean = run.getBean(AppBean2.class);
		System.out.println(configurableBean.getMessage());
		
		// ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		// isAllowBeanDefinitionOverriding() = true (vì khởi tạo context bằng từ khóa news)
		ApplicationContext context = new AnnotationConfigApplicationContext(Main2.class);
		AppBean2 bean = context.getBean(AppBean2.class);
		System.out.println(bean.getClass().getName());
	}
}
