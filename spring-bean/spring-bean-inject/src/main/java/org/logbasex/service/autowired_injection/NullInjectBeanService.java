package org.logbasex.service.autowired_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NullInjectBeanService {
	@Autowired
	private AutowiredInjectBeanService autowiredInjectBeanSv;
	
	public Object inject() {
		return autowiredInjectBeanSv.inject();
	}
}
