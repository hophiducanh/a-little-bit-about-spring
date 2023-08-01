package com.logbasex.multithreading.basic.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrencyUtilitiesExample {
	public static void main(String[] args) {
		Runnable task = () -> {
			System.out.println("Task running on thread: " + Thread.currentThread().getName());
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		
		for (int i = 0; i < 5; i++) {
			executorService.execute(task);
		}
		
		executorService.shutdown();
	}
}
