package com.logbasex.multithreading.basic.runnable;

/**
 * Explanation:
 * This code uses the Runnable interface to create and run a thread. The SimpleRunnable class implements the Runnable
 * interface and overrides the run() method. The Thread class is then used to create a thread, passing an instance of
 * SimpleRunnable, and the start() method is called to start the thread's execution.
 * <p>
 * Advantage of Multithreading:
 * Using Runnable allows better separation of concerns, as the thread's behavior is defined in a separate class,
 * making the code more modular and maintainable. In contrast, traditional approaches might involve placing all the
 * code in the same method, leading to more challenging maintenance and code readability.
 */
public class SimpleRunnable implements Runnable {
	public void run() {
		System.out.println("Hello from a thread using Runnable!");
	}
	
	public static void main(String[] args) {
		SimpleRunnable simpleRunnable = new SimpleRunnable();
		Thread thread = new Thread(simpleRunnable);
		thread.start();
	}
}
