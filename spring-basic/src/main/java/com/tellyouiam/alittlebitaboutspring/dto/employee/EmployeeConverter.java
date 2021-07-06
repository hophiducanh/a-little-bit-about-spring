package com.tellyouiam.alittlebitaboutspring.dto.employee;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.stereotype.Component;


//https://www.baeldung.com/configuration-properties-in-spring-boot
@Component
@ConfigurationPropertiesBinding
public class EmployeeConverter implements Converter<String, Employee> {

	@Override
	public Employee convert(String value) {
		String[] data = value.split(",");
		return new Employee(data[0], Double.parseDouble(data[1]));
	}
	
	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		return null;
	}
	
	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		return null;
	}
}
