package com.logbasex.multithreading.basic.synchronization;

/**
 * Explanation:
 * This code demonstrates a scenario where two threads increment a shared counter concurrently. The increment()
 * method is synchronized to ensure that only one thread can modify the count variable at a time, preventing race
 * conditions.
 * <p>
 * Advantage of Multithreading:
 * By synchronizing access to shared resources, we ensure thread safety and prevent data corruption. Traditional
 * approaches may not handle concurrent access to shared data correctly, leading to incorrect results or even program
 * crashes.
 */
public class SynchronizationExample {
	public static class Counter {
		private int count = 0;
		
		// In Java, the synchronized keyword is used to provide a mechanism for controlling access to critical sections
		// of code, known as synchronized blocks or synchronized methods. It ensures that only one thread can access
		// the synchronized code block or method at a time, preventing concurrent access and potential data corruption
		// or race conditions.
		public synchronized void increment() {
			count++;
		}
		
		public int getCount() {
			return count;
		}
	}
	
	public static void main(String[] args) {
		Counter counter = new Counter();
		
		Runnable runnable = () -> {
			for (int i = 0; i < 100000; i++) {
				counter.increment();
			}
		};
		
		Thread thread1 = new Thread(runnable);
		Thread thread2 = new Thread(runnable);
		
		thread1.start();
		thread2.start();
		
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Count: " + counter.getCount());
	}
}
