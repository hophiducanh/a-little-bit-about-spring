package com.logbasex.imperative.service;

import com.logbasex.imperative.dto.Data;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CustomerService {
	
	public Data getData(long threadId, long time) {
		try {
			Thread.sleep(1000);
			return Data.builder().data("Some Data").threadId(threadId).requestCameTime(time).build();
		} catch (InterruptedException e) {
			throw new RuntimeException("error happened");
		}
	}
	
	@Async
	public void getDataV3(long threadId, long time) {
		try {
			Thread.sleep(1000);
			Data.builder().data("Some Data").threadId(threadId).requestCameTime(time).build();
		} catch (InterruptedException e) {
			throw new RuntimeException("error happened");
		}
	}
	
	@Async
	public CompletableFuture<Data> getDataAsync(long threadId, long time) {
		try {
			Thread.sleep(1000);
			return CompletableFuture.supplyAsync(
					() -> Data.builder().data("Some Data").threadId(threadId).requestCameTime(time).build()
			);
		} catch (InterruptedException e) {
			throw new RuntimeException("error happened");
		}
	}
	
	@Async
	public CompletableFuture<Data> getDataAsyncV2(long threadId, long time) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(1000);
				return Data.builder().data("Some Data").threadId(threadId).requestCameTime(time).build();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
