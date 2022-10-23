package com.logbasex.aop.controller;

import com.logbasex.aop.repository.UserRepository;
import com.logbasex.aop.service.IUserService;
import com.logbasex.aop.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private UserRepository userRepo;

	//proxy error with jdk dynamic proxy, due to UserServiceImpl is concrete class, not interface.
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@GetMapping("/hello")
	public void hello() {
		// https://programming.vip/docs/61c5b67575556.html
		//iUserService DEFAULT is wrap around using CGLIB proxy, debug for detail information.
		//if you want to use JDK proxy, pls set: proxy-target-class = false
		iUserService.hello();
		userRepo.findAll();
		userServiceImpl.hello();
	}
}
