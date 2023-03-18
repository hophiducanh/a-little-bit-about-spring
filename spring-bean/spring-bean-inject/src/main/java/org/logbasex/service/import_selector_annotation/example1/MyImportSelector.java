package org.logbasex.service.import_selector_annotation.example1;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelector implements ImportSelector {
	@Override
	public String[] selectImports (AnnotationMetadata importingClassMetadata) {
		String prop = System.getProperty("myProp");
		if ("1".equals(prop)) {
			return new String[]{MyConfig1.class.getName()};
		} else {
			return new String[]{MyConfig2.class.getName()};
		}
	}
}
