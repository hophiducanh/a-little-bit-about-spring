package com.logbasex.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class _404Controller {
	
	@GetMapping("/404")
	public Object demo404NotFound(){
		return "404 not found";
	}
}
