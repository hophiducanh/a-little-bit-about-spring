package org.logbasex;

import org.logbasex.service.import_annotation.ImportConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(
//		scanBasePackages = {
//				"org.logbasex.service",
//				"org.logbasex.controller"
//		}
//		scanBasePackageClasses = {
//				InjectBeanController.class,
//				Person.class,
//				org.logbasex.service.import_annotation.Person.class
//		}
)
@ComponentScan(excludeFilters=@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes =
		{
				ImportConfiguration.class
		}))
public class SpringBeanInjectApplication {
	public static void main(String[] args) {
		System.setProperty("myProp", "1");
		SpringApplication.run(SpringBeanInjectApplication.class, args);
	}
}
