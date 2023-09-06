package com.logbasex.reactive.service;

import com.logbasex.reactive.dto.Data;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	
	public Data getData(long threadId, long time) {
		try {
			Thread.sleep(1000);
			return Data.builder().data("Some Data").threadId(threadId).requestCameTime(time).build();
		} catch (InterruptedException e) {
			throw new RuntimeException("error happened");
		}
	}
	
}
