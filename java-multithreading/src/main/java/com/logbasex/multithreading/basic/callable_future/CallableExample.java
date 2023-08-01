package com.logbasex.multithreading.basic.callable_future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableExample {
	public static void main(String[] args) {
		Callable<String> callable = () -> {
			Thread.sleep(2000);
			return "Hello from Callable!";
		};
		
		FutureTask<String> futureTask = new FutureTask<>(callable);
		new Thread(futureTask).start();
		
		try {
			String result = futureTask.get();
			System.out.println(result);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
