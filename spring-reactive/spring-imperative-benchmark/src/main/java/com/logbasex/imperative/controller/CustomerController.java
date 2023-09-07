package com.logbasex.imperative.controller;

import com.logbasex.imperative.dto.Customer;
import com.logbasex.imperative.dto.Data;
import com.logbasex.imperative.repository.CustomerRepository;
import com.logbasex.imperative.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerService customSv;
	private final CustomerRepository customerRepo;
	
	@GetMapping(value = "/data")
	public Data getData() {
		return customSv.getData(Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
	@RequestMapping("/sync/{id}")
	public Customer get(@PathVariable(value = "id") String id) {
		return customerRepo.findById(id).orElse(null);
	}
	
	@PostMapping("/sync")
	public Customer create(@RequestBody Customer customer) {
		return customerRepo.save(customer);
	}
}
