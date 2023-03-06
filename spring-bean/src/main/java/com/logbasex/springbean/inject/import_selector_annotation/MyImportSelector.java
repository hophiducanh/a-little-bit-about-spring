package com.logbasex.springbean.inject.import_selector_annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelector implements ImportSelector {
	
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{"com.logbasex.springbean.inject.import_selector_annotation.person"};
	}
}
