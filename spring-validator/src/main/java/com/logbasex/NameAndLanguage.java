package com.logbasex;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameAndLanguageValidator.class)
public @interface NameAndLanguage {
	
	String message() default "Name and language must not equal!.";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
