package com.logbasex.ioc_di.controller;

import com.logbasex.ioc_di.service.UserService;
import com.logbasex.ioc_di.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@Autowired
	private UserServiceImpl userImplSv;
	
	@Autowired
	private UserService userSv;
	
	@GetMapping("/hello")
	public Object sayHello() {
		return ResponseEntity.ok(userImplSv.sayHello());
	}
}
