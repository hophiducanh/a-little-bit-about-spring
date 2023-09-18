package com.logbasex.webflux.service;

import com.logbasex.webflux.dao.CustomerDao;
import com.logbasex.webflux.dto.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerDao customerDao;
	
	public List<Customer> loadAlLCustomers() {
		long start = System.currentTimeMillis();
		List<Customer> customers = customerDao.getCustomers();
		long end = System.currentTimeMillis();
		System.out.println("Total execution time " + (end - start) + " ms");
		return customers;
	}
	
	public Flux<Customer> loadAlLCustomersStream() {
		long start = System.currentTimeMillis();
		Flux<Customer> customers = customerDao.getCustomersStream();
		long end = System.currentTimeMillis();
		System.out.println("Total execution time " + (end - start) + " ms");
		return customers;
	}
}
