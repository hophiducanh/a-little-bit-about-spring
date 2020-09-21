package com.tellyouiam.alittlebitaboutspring.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@ComponentScan(basePackages = { "com.tellyouiam.alittlebitaboutspring.rest" })
public class WebConfig implements WebMvcConfigurer {
	
	//https://dzone.com/articles/customizing
	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
		builder.serializationInclusion(JsonInclude.Include.NON_NULL);
		return builder;
	}
}
