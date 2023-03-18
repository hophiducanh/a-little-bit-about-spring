package org.logbasex.service.import_selector_annotation.example2;

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
		// set isAllowBeanDefinitionOverriding() = false rồi mới khởi tạo.
		ConfigurableApplicationContext run = SpringApplication.run(Main.class, args);
		AppBean configurableBean = run.getBean(AppBean.class);
		System.out.println(configurableBean.getMessage());
		
		// ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		// isAllowBeanDefinitionOverriding() = true (vì khởi tạo context bằng từ khóa news)
		ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		AppBean bean = context.getBean(AppBean.class);
		System.out.println(bean.getClass().getName());
	}
}
