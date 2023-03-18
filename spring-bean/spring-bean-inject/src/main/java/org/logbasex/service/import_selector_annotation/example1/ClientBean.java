package org.logbasex.service.import_selector_annotation.example1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientBean {
	@Autowired
	private AppBean appBean;
	
	public String doSomething () {
		return appBean.getMessage();
	}
}
