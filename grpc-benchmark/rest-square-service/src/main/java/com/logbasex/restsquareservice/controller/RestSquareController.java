package com.logbasex.restsquareservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestSquareController {
	
	@GetMapping("/rest/square/unary/{number}")
	public int getSquareUnary(@PathVariable int number){
		return number * number;
	}
}
