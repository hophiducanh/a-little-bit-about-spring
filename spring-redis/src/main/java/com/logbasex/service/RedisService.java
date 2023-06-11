package com.logbasex.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	
	//first time call API the debugging point is hit, but not getting hit in another time.
	@Cacheable("fibonacci")
	public int fibonacci(int n) {
		if (n < 2) {
			return n;
		}
		return fibonacci(n - 1) + fibonacci(n - 2);
	}
}
