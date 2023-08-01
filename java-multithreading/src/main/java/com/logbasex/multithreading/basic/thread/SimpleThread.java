package com.logbasex.multithreading.basic.thread;

/**
 * Explanation:
 * This code demonstrates the basics of creating and running a thread. The SimpleThread class extends the Thread
 * class and overrides the run() method to specify the task to be executed by the thread. The start() method is
 * called to start the thread's execution.
 * <p>
 * Advantage of Multithreading:
 * The advantage here is that we can perform multiple tasks concurrently, allowing us to achieve parallelism and
 * better utilization of CPU resources. In traditional single-threaded programming, tasks would have to be executed
 * sequentially, leading to potentially slower performance.
 */
public class SimpleThread extends Thread {
	public void run() {
		System.out.println("Hello from a thread!");
	}
	
	public static void main(String[] args) {
		SimpleThread thread = new SimpleThread();
		thread.start();
	}
}
