package org.logbasex.service.import_selector_annotation;

import org.springframework.beans.factory.annotation.Autowired;

public class ImportSelectorPersonService {
	@Autowired
	private ImportSelectorPerson importSelectorPerson;
	
	public Object inject() {
		return importSelectorPerson.getName();
	}
}
