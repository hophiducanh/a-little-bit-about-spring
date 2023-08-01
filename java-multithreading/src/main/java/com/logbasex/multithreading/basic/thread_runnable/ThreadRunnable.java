package com.logbasex.multithreading.basic.thread_runnable;

/**
 * In Java, there are two ways to create a thread: by extending the Thread class or by implementing the Runnable
 * interface.
 */
public class ThreadRunnable {
	
	/**
	 * When implementing interface Runnable it means you are creating something which is run able in a different
	 * thread. Now creating something which can run inside a thread (runnable inside a thread), doesn't mean to
	 * creating a Thread.
	 * So the class MyRunnable is nothing but a ordinary class with a void run method. And it's objects will be some
	 * ordinary objects with only a method run which will execute normally when called. (unless we pass the object in
	 * a thread).
	 */
	public static class MyRunnable implements Runnable {
		@Override
		public void run() {
			for (int i = 1; i <= 5; i++) {
				System.out.println(Thread.currentThread().getName() + ": " + i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * class Thread, I would say A very special class with the capability of starting a new Thread which actually
	 * enables multi-threading through its start() method.
	 */
	public static class MyThread extends Thread {
		public void run() {
			for (int i = 1; i <= 5; i++) {
				System.out.println(Thread.currentThread().getName() + ": " + i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Why not wise to compare?
	 * Because we need both of them for multi-threading.
	 * <p>
	 * For Multi-threading we need two things:
	 * <p>
	 * Something that can run inside a Thread (Runnable).
	 * Something That can start a new Thread (Thread).
	 */
	public static void main(String[] args) {
		MyRunnable myRunnable = new MyRunnable();
		Thread threadRunnable1 = new Thread(myRunnable, "Thread 1");
		Thread threadRunnable2 = new Thread(myRunnable, "Thread 2");
		
		threadRunnable1.start();
		threadRunnable2.start();
		
		MyThread thread1 = new MyThread();
		MyThread thread2 = new MyThread();
		
		thread1.start();
		thread2.start();
	}
}
