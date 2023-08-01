package com.logbasex.multithreading.basic.thread_pools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Explanation:
 * This code example is similar to the previous one, but instead of explicitly creating an ExecutorService, it uses
 * Executors.newFixedThreadPool(2) to create a fixed-size thread pool.
 * <p>
 * Advantage of Multithreading:
 * Using thread pools with the ExecutorService provides better control over the number of threads created, ensuring
 * that the system doesn't get overloaded with too many threads. Traditional approaches may involve manually creating
 * and managing threads, which can be error-prone and less efficient.
 */
public class ThreadPoolExample {
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
