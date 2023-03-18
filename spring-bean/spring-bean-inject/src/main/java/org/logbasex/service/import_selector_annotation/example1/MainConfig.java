package org.logbasex.service.import_selector_annotation.example1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

//No qualifying bean if comment @Import
@Configuration
@Import(MyImportSelector.class)
public class MainConfig {
	@Bean
	ClientBean clientBean() {
		return new ClientBean();
	}
}
