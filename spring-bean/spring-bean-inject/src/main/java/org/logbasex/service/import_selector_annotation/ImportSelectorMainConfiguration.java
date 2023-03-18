package org.logbasex.service.import_selector_annotation;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MyImportSelector.class)
public class ImportSelectorMainConfiguration {
	
	@Bean
	public ImportSelectorPersonService importSelectorPersonService() {
		return new ImportSelectorPersonService();
	}
}
