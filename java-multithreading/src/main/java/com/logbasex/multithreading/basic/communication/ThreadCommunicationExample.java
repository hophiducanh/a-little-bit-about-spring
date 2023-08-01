package com.logbasex.multithreading.basic.communication;

/**
 * Explanation:
 * This code demonstrates communication between two threads using the Message class. One thread writes a message
 * using write() method, and the other thread reads it using the read() method. The methods are synchronized, and
 * wait() and notifyAll() are used to coordinate the communication.
 * <p>
 * Advantage of Multithreading:
 * Thread communication allows threads to exchange information and coordinate their activities. In traditional
 * approaches, such communication might involve complex inter-process communication mechanisms, leading to more
 * complex code and potential synchronization issues.
 */
public class ThreadCommunicationExample {
	public static void main(String[] args) {
		Message message = new Message();
		
		Thread writerThread = new Thread(() -> {
			String[] messages = {"Hello", "World", "Java", "Multithreading"};
			for (String msg : messages) {
				message.write(msg);
				System.out.println("Sent: " + msg);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread readerThread = new Thread(() -> {
			for (int i = 0; i < 4; i++) {
				String receivedMsg = message.read();
				System.out.println("Received: " + receivedMsg);
			}
		});
		
		writerThread.start();
		readerThread.start();
	}
}
