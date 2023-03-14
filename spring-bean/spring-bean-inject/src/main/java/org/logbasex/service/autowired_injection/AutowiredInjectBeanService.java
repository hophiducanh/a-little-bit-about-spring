package org.logbasex.service.autowired_injection;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AutowiredInjectBeanService {
	public Object inject() {
		return new Date();
	}
}
