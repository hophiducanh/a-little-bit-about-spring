package com.logbasex.async_annotation.controller;

import com.logbasex.async_annotation.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
	
	private final HelloService helloService;
	
	public HelloWorldController(HelloService helloService) {
		this.helloService = helloService;
	}
	
	@GetMapping("/hello")
	public String hello() {
		long start = System.currentTimeMillis();
		helloService.processSomethingForLong();
		long end = System.currentTimeMillis();
		return "Hello World Took " + (end - start) + " milliseconds ! and the current Thread is : "+Thread.currentThread().getName();
	}
	
	@GetMapping("/hello/async")
	public String helloAsync() {
		long start = System.currentTimeMillis();
		// nếu 1 method trong service có đi kèm @Async annotation hay @Transactional thì helloService sẽ được inject
		// qua proxy. Ngược lại thì không.
		helloService.asyncProcessSomethingForLong();
		long end = System.currentTimeMillis();
		return "Hello World Took " + (end - start) + " milliseconds ! and the current Thread is : "+Thread.currentThread().getName();
	}
	
}
