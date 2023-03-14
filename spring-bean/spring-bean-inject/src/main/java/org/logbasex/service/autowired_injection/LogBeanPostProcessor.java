package org.logbasex.service.autowired_injection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LogBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		String out = String.format("Bean instantiated with name %s and class %s", beanName, bean.getClass().getSimpleName());
		System.out.println(out);
		return bean;
	}
}
