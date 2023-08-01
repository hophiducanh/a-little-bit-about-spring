package com.logbasex.multithreading.basic.producer_consumer;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumerExample {
	public static void main(String[] args) {
		Queue<Integer> queue = new LinkedList<>();
		int maxSize = 5;
		
		Runnable producer = () -> {
			int count = 1;
			while (true) {
				synchronized (queue) {
					while (queue.size() == maxSize) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					queue.offer(count);
					System.out.println("Produced: " + count);
					count++;
					queue.notifyAll();
				}
			}
		};
		
		Runnable consumer = () -> {
			while (true) {
				synchronized (queue) {
					while (queue.isEmpty()) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					int value = queue.poll();
					System.out.println("Consumed: " + value);
					queue.notifyAll();
				}
			}
		};
		
		Thread producerThread = new Thread(producer);
		Thread consumerThread = new Thread(consumer);
		
		producerThread.start();
		consumerThread.start();
	}
}
