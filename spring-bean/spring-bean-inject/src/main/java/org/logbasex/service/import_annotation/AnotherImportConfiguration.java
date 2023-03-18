package org.logbasex.service.import_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(ImportConfiguration.class)
@Configuration
public class AnotherImportConfiguration {
	
	// alternative for @ComponentScan. (if @ComponentScan exclude ImportConfiguration.class then you cannot
	// initialize AnotherImportPerson bean)
	// @ComponentScan(excludeFilters=@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
	// ImportConfiguration.class))
	//
	// https://stackoverflow.com/questions/35502164/what-is-the-use-case-of-import-annotation
	// Về cơ bản nếu @SpringBootApplication ở package org.logbasex thì nếu config ở package org.config sẽ không dùng
	// @ComponentScan được mà phải dùng @Import. Nó thực sự hiệu quả cho modularization.
	@Bean
	public AnotherImportPerson anotherImportPerson(ImportPerson importPerson) {
		AnotherImportPerson anotherImportPerson = new AnotherImportPerson();
		anotherImportPerson.setName(String.join(" ", anotherImportPerson.getName(), importPerson.getName()));
		return anotherImportPerson;
	}
}
