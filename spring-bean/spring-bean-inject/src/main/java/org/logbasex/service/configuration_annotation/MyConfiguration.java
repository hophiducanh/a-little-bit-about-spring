package org.logbasex.service.configuration_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {
	@Bean
	public ConfigurationPerson configurationPerson() {
		ConfigurationPerson configurationPerson = new ConfigurationPerson();
		configurationPerson.setName("spring");
		return configurationPerson;
	}
}
