package com.logbasex.reactive.service;

import com.logbasex.reactive.dto.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ReactiveCustomerService {
	
	public Mono<Data> getData(long threadId, long time) {
		Data data = Data.builder()
				.data("Some Data")
				.threadId(threadId)
				.requestCameTime(time)
				.build();
		
		return Mono.just(data).delayElement(Duration.ofSeconds(1));
	}
	
}
