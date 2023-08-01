package com.logbasex.multithreading.basic.communication;

public class Message {
	private String content;
	private boolean available = false;
	
	public synchronized String read() {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		available = false;
		notifyAll();
		return content;
	}
	
	public synchronized void write(String content) {
		while (available) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.content = content;
		available = true;
		notifyAll();
	}
}
