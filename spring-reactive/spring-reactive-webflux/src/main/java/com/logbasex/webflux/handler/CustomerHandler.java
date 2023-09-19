package com.logbasex.webflux.handler;

import com.logbasex.webflux.dao.CustomerDao;
import com.logbasex.webflux.dto.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerHandler {
	
	private final CustomerDao customerDao;
	
	public Mono<ServerResponse> loadCustomers(ServerRequest request) {
		Flux<Customer> customerList = customerDao.getCustomerList();
		return ServerResponse
				.ok()
				.body(customerList, Customer.class);
	}
	
	public Mono<ServerResponse> findCustomer(ServerRequest serverRequest) {
		int customerId = Integer.parseInt(serverRequest.pathVariable("input"));
		Mono<Customer> customerMono = customerDao
				.getCustomerList()
				.filter(c -> c.getId() == customerId)
				.next();
		
		return ServerResponse
				.ok()
				.body(customerMono, Customer.class);
	}
	
	public Mono<ServerResponse> saveCustomer(ServerRequest request) {
		Mono<Customer> customerMono = request.bodyToMono(Customer.class);
		Mono<String> response = customerMono.map(customer -> customer.getId() + ":" + customer.getName());
		
		return ServerResponse
				.ok()
				.body(response, Mono.class);
	}
}
