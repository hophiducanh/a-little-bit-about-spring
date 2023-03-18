package org.logbasex.service.import_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportConfiguration {
	
	@Bean
	public ImportPerson importPerson() {
		return new ImportPerson();
	}
}
