package com.logbasex.reactive.controller;

import com.logbasex.reactive.dto.Data;
import com.logbasex.reactive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerService customSv;
	
	@GetMapping(value = "/data")
	public Data getData() {
		return customSv.getData(Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
}
