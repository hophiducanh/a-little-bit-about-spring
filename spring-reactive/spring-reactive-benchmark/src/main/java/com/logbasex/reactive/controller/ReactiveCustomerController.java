package com.logbasex.reactive.controller;

import com.logbasex.reactive.dto.Data;
import com.logbasex.reactive.service.ReactiveCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ReactiveCustomerController {
	private final ReactiveCustomerService reactiveCustomerSv;
	
	@GetMapping(value = "/reactive/data")
	public Mono<Data> getData(){
		return reactiveCustomerSv.getData(Thread.currentThread().getId(), System.currentTimeMillis());
	}
}
