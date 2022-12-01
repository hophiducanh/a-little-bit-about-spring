package com.logbasex;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAndLanguageValidator implements ConstraintValidator<NameAndLanguage, Object> {
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		return false;
	}
}
