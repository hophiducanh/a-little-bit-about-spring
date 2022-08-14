package com.logbasex.aop.controller;

import com.logbasex.aop.repository.UserRepository;
import com.logbasex.aop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/hello")
	public void hello() {
		// https://programming.vip/docs/61c5b67575556.html
		//iUserService DEFAULT is wrap around using CGLIB proxy, debug for detail information.
		//if you want to use JDK proxy, pls set: proxy-target-class = false
		iUserService.hello();
		userRepo.findById("");
	}
}
