package org.logbasex.controller;

import org.logbasex.service.autowired_injection.AutowiredInjectBeanService;
import org.logbasex.service.autowired_injection.NullInjectBeanService;
import org.logbasex.service.component_annotation.ComponentPerson;
import org.logbasex.service.configuration_annotation.ConfigurationPerson;
import org.logbasex.service.constructor_injection.ConstructorInjectBeanService;
import org.logbasex.service.import_annotation.AnotherImportPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InjectBeanController {
	
	private final ConstructorInjectBeanService constructorInjectBeanSv;
	
	public InjectBeanController(ConstructorInjectBeanService constructorInjectBeanSv) {
		this.constructorInjectBeanSv = constructorInjectBeanSv;
	}
	
	@Autowired
	private AutowiredInjectBeanService autowiredInjectBeanSv;
	
	@Autowired
	private ComponentPerson componentAnnotationInject;
	
	@Autowired
	private ConfigurationPerson configurationAnnotationInject;
	
	@Autowired
	private AnotherImportPerson importAnnotationInject;
	
//	@Autowired
//	private ImportSelectorMainConfiguration importSelectorInject;
	
	@GetMapping("/inject/constructor")
	public Object constructorInject(){
		return constructorInjectBeanSv.inject();
	}
	
	@GetMapping("/inject/autowired")
	public Object autowiredInject(){
		return autowiredInjectBeanSv.inject();
	}
	
	@GetMapping("/inject/autowired/null")
	public Object nullAutowiredInject(){
		// Actually, you should use either JVM managed Objects or Spring-managed Object to invoke methods.
		// https://stackoverflow.com/questions/19896870/why-is-my-spring-autowired-field-null
		NullInjectBeanService nullInjectBeanService = new NullInjectBeanService();
		
		// https://www.baeldung.com/spring-autowired-field-null
		// What happened here? When we called the NullInjectBeanService constructor in our controller, we created an object that
		// is not managed by Spring.
		// Having no clue of the existence of this NullInjectBeanService object, Spring is not able to inject a AutowiredInjectBeanService bean inside it.
		// Thus, the AutowiredInjectBeanService instance inside the NullInjectBeanService object we created will remain null, causing the NullPointerException we get when we try to call a method on this object.
		
		// LogBeanPostProcessor khi log thì bean vẫn được tạo nhưng không inject được.
		return nullInjectBeanService.inject();
	}
	
	@GetMapping("/inject/component-annotation")
	public Object componentAnnotationInject(){
		return componentAnnotationInject.getName();
	}
	
	@GetMapping("/inject/configuration-annotation")
	public Object configurationAnnotationInject(){
		return configurationAnnotationInject.getName();
	}
	
	@GetMapping("/inject/import-annotation")
	public Object importAnnotationInject(){
		return importAnnotationInject.getName();
	}
	
//	@GetMapping("/inject/import-selector")
//	public Object importSelector(){
//		return importSelectorInject.importSelectorPersonService().inject();
//	}
}
