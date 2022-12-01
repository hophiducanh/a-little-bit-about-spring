package com.logbasex;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageValidator implements ConstraintValidator<Language, String> {
	@Autowired
	private LanguageProvider languageProvider;
	
	@Override
	public void initialize(Language constraintAnnotation) {
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		return languageProvider.getLanguages()
				.stream()
				.anyMatch(value::equalsIgnoreCase);
	}
}
