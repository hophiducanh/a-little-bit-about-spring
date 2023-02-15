package com.logbasex.async_annotation.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
	public void processSomethingForLong() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("I take 10 seconds to complete on a Thread named : " + Thread.currentThread().getName());
	}
	
	
	/**
	 * Tóm tắt lại thì method được tag **@Async** (được gọi là callee), khi được gọi từ 1 process (được gọi là caller)
	 * **sẽ được thực hiện ở 1 thread mới**. Caller sẽ không chờ callee được thực thi xong rồi mới thực hiện tiếp
	 * (việc chờ rồi mới thực thi tiếp này gọi là đồng bộ - synchronize), mà cứ tiếp tục công việc của caller.
	 * Lúc đó, callee được chạy bất đồng bộ.
	 */
	@Async
	public void asyncProcessSomethingForLong() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
