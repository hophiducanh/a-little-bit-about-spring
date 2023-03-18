package org.logbasex.service.import_selector_annotation.example1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig2 {
	@Bean
	AppBean appBean () {
		return new AppBean("from config 2");
	}
}
