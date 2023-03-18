package org.logbasex.service.import_selector_annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelector implements ImportSelector {
	
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		String prop = System.getProperty("myProp");
		if ("1".equals(prop)) {
			return new String[]{ImportSelectorConfiguration1.class.getName()};
		} else {
			return new String[]{ImportSelectorConfiguration2.class.getName()};
		}
	}
}
