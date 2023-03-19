package org.logbasex.service.import_selector_annotation.example2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig4 {
	@Bean
	AppBean2 appBean2() {
		return new AppBean2("from config 2");
	}
}
