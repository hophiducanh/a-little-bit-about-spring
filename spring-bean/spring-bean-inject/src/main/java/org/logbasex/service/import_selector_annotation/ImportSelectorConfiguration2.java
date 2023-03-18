package org.logbasex.service.import_selector_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportSelectorConfiguration2 {
	@Bean
	public ImportSelectorPerson importSelectorPerson() {
		return new ImportSelectorPerson("ImportSelector Configuration 2.");
	}
}
