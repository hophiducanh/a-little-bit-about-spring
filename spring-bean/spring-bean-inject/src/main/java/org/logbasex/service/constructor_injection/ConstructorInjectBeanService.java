package org.logbasex.service.constructor_injection;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ConstructorInjectBeanService {
	public Object inject() {
		return new Date();
	}
}
