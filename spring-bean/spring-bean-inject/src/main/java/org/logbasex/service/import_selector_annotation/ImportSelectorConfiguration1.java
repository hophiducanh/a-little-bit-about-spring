package org.logbasex.service.import_selector_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportSelectorConfiguration1 {
	@Bean
	public ImportSelectorPerson importSelectorPerson1() {
		return new ImportSelectorPerson("ImportSelector Configuration 1.");
	}
}
