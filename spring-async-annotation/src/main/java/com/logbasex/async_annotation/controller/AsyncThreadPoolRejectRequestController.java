package com.logbasex.async_annotation.controller;

import com.logbasex.async_annotation.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.RejectedExecutionException;

@RestController
public class AsyncThreadPoolRejectRequestController {
	private final HelloService helloService;
	
	public AsyncThreadPoolRejectRequestController(HelloService helloService) {
		this.helloService = helloService;
	}
	
	/**
	 * <a href="https://medium.com/@kswastik29/common-mistakes-to-avoid-when-using-async-in-spring-1eef0e8d15fb">...</a>
	 * <p>
	 * Custom thread pool set maxPoolSize = 6, tức là chỉ xử lý được tối đa 6 request đồng thời.
	 * Sau khi init, call async API thì sẽ print ra kết quả như sau (trong 100 millis đầu tiên sẽ xử lý được 6
	 * request, nhét 5 cái còn lại vào queue):
	 * 1
	 * 2
	 * 8
	 * 9
	 * 10
	 * 11
	 * <p>
	 * Khi đó async API sẽ bị reject vì thread pool đạt max. Nhưng sau 1 phút (xử lý xong 6 request đầu tiên) thì
	 * thread được giải phóng và sẽ tiếp tục print những giá trị còn lại trong queue (dequeue) như sau:
	 * <p>
	 * 3
	 * 4
	 * 5
	 * 6
	 * 7
	 */
	@PostConstruct
	public void init() throws InterruptedException {
		for (int i = 1; i <= 11; i++) {
			helloService.printNumber(i);
			Thread.sleep(100);
		}
	}
	
	@GetMapping("/async/{number}")
	public void async(@PathVariable long number) {
		try {
			helloService.printNumber(number);
		} catch (RejectedExecutionException e) {
			System.out.println("Rejecting task since queue is full and no threads are free for task number: " + number);
		}
	}
}
