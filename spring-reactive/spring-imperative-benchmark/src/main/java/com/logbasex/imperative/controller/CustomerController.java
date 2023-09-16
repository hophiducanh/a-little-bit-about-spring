package com.logbasex.imperative.controller;

import com.logbasex.imperative.dto.Customer;
import com.logbasex.imperative.dto.Data;
import com.logbasex.imperative.repository.CustomerRepository;
import com.logbasex.imperative.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerService customSv;
	private final CustomerRepository customerRepo;
	
	@GetMapping(value = "/data")
	public Data getData() {
		return customSv.getData(Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
	/**
	 * <a href="https://stackoverflow.com/questions/65682194/what-are-impact-to-my-spring-boot-application-if-i-have-task-executor">...</a>
	 * <a href="https://stackoverflow.com/questions/65120202/is-using-async-and-completablefuture-in-controller-can-increase-performance-of/65185737#65185737">...</a>
	 */
//	@Async
	@GetMapping(value = "/async/data")
	public CompletableFuture<Data> getAsyncData() {
		return customSv.getDataAsync(Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
	/**
	 * why async does not work? execute takes 1 sec because return CompletableFuture.
	 */
	@GetMapping(value = "/async/data/v2")
	public CompletableFuture<Data> getAsyncDataV2() {
		return customSv.getDataAsyncV2(Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
	/**
	 * async work. execute takes x milliseconds.
	 * <a href="https://stackoverflow.com/questions/58505549/how-does-spring-get-the-result-from-an-endpoint-that-returns-completablefuture-o?noredirect=1&lq=1">...</a>
	 */
	@GetMapping(value = "/async/data/v3")
	public void getAsyncDataV3() {
		customSv.getDataV3(Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
	/**
	 * Async work.
	 */
	@GetMapping(value = "/async/data/v4")
	public void getAsyncDataV4() {
		CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(1000); // Sleep for 1 second (does not block the main thread)
				return Data.builder().data("Some Data").threadId(Thread.currentThread().getId()).requestCameTime(System.currentTimeMillis()).build();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	/**
	 * Async doesn't work if return CompletableFuture.
	 */
	@GetMapping(value = "/async/data/v5")
	public CompletableFuture<Data> getAsyncDataV5() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(1000); // block main thread
				return Data.builder().data("Some Data").threadId(Thread.currentThread().getId()).requestCameTime(System.currentTimeMillis()).build();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
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
