package com.logbasex.aggregatorservice.controller;

import com.logbasex.aggregatorservice.service.RestAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("rest")
public class RestAPIController {
	
	@Autowired
	private RestAPIService restAPISv;
	
	@GetMapping("/unary/{number}")
	public Object getResponseUnary(@PathVariable int number){
		try {
			return this.restAPISv.getUnaryResponse(number);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
