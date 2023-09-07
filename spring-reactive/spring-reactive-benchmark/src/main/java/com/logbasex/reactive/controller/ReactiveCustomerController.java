package com.logbasex.reactive.controller;

import com.logbasex.reactive.dto.Customer;
import com.logbasex.reactive.dto.Data;
import com.logbasex.reactive.repository.ReactiveCustomerRepository;
import com.logbasex.reactive.service.ReactiveCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ReactiveCustomerController {
	private final ReactiveCustomerService reactiveCustomerSv;
	private final ReactiveCustomerRepository reactiveCustomerRepo;
	
	@GetMapping(value = "/reactive/data")
	public Mono<Data> getData(){
		return reactiveCustomerSv.getData(Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
	@RequestMapping("/reactive/{id}")
	public Mono<Customer> get(@PathVariable(value = "id") String id) {
		return reactiveCustomerRepo.findById(id);
	}
	
	@PostMapping("/reactive")
	public Mono<Customer> create(@RequestBody Customer customer) {
		return reactiveCustomerRepo.save(customer);
	}
}
