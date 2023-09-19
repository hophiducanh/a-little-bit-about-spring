package com.logbasex.webflux.handler;

import com.logbasex.webflux.dao.CustomerDao;
import com.logbasex.webflux.dto.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerStreamHandler {
	
	private final CustomerDao customerDao;
	
	public Mono<ServerResponse> loadCustomersStream(ServerRequest request) {
		Flux<Customer> customersStream = customerDao.getCustomersStream();
		return ServerResponse
				.ok()
				.contentType(MediaType.TEXT_EVENT_STREAM) //return stream instead of object.
				.body(customersStream, Customer.class);
	}
}
