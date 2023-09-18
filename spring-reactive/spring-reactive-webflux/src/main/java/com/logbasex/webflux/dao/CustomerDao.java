package com.logbasex.webflux.dao;

import com.logbasex.webflux.dto.Customer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CustomerDao {
	
	private void sleep(int i) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	//takes 10 seconds.
	public List<Customer> getCustomers() {
		return IntStream
				.rangeClosed(1, 10)
				.peek(this::sleep)
				.peek(i -> System.out.println("processing count: " + i))
				.mapToObj(i -> new Customer(i, "customer" + i))
				.collect(Collectors.toList());
	}
	
	public Flux<Customer> getCustomersStream() {
		return Flux
				.range(1, 10)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> System.out.println("reactive processing count: " + i))
				.map(i -> new Customer(i, "customer" + i));
	}
}
