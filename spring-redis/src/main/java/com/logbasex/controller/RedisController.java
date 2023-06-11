package com.logbasex.controller;

import com.logbasex.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {
	
	@Autowired
	private RedisService redisSv;
	
	@GetMapping("/redis")
	public Object restRedisCache(@RequestParam int n){
		return redisSv.fibonacci(n);
	}
}
