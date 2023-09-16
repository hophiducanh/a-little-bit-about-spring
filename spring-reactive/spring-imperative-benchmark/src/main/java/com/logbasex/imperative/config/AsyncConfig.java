package com.logbasex.imperative.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
	
	/**
	 * spring-boot-autoconfigure/spring-configuration-metadata.json
	 * <p>
	 * {
	 *    "name": "server.tomcat.threads.max",
	 *    "type": "java.lang.Integer",
	 *    "description": "Maximum amount of worker threads.",
	 *    "sourceType": "org.springframework.boot.autoconfigure.web.ServerProperties$Tomcat$Threads",
	 *    "defaultValue": 200
	 * }
	 * <p>
	 * By default, Spring will be searching for an associated thread pool definition: either a unique TaskExecutor
	 * bean in the context, or an Executor bean named “taskExecutor” otherwise. If neither of the two is resolvable, a
	 * SimpleAsyncTaskExecutor will be used to process async method invocations.
	 * <p>
	 * <a href="https://www.linkedin.com/pulse/minute-read-configuring-task-executor-spring-boot-large-bhandari/">...</a>
	 */
	@Primary
	@Override
	@Bean("taskExecutorDefault")
	public AsyncTaskExecutor getAsyncExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// Set the prefix for thread names (helpful for debugging).
		executor.setThreadNamePrefix("my-exec-async");
		
		// Set the core pool size (minimum number of threads).
//		executor.setCorePoolSize(5);
		executor.setCorePoolSize(5);
		
		// Set the maximum pool size (maximum number of threads).
//		executor.setMaxPoolSize(100);
		executor.setMaxPoolSize(100);
		
		// Set the queue capacity. Tasks exceeding this capacity will be queued.
		executor.setQueueCapacity(1000);
		
		executor.initialize();
		return executor;
		
		// Bình thường sẽ sử dụng 5 thread (setCorePoolSize) nhưng quá tải sẽ cho vào hàng đợi. Đợi đến 1000 thì sẽ
		// tạo thread mới.
		// Maximum 100 thread mới (setMaxPoolSize).
		// Trong trường hợp bình thường sẽ chỉ dùng 5 thread để tiết kiệm. Nếu quá tải thì có thể sử dụng maximum 100
		// thread.
	}
	
	/**
	 * Ở trên chỉ chạy maximum được 100 async task, nếu quá thì những request tiếp theo sẽ được nhét vào queue cho
	 * đến maximum = 1000.
	 * Nhưng vấn đề là nếu 100 async task này đều là long-running task thì 1000 task khác trong queue sẽ bị block
	 * (không rõ là block and rejected, cần phải test thêm),
	 * do đó cần tạo ra một thread-pool khác cho long-running tasks.
	 * <p>
	 * <a href="https://medium.com/@dvikash1001/springboot-async-the-magic-and-the-gotchas-17f9471c6fe4">...</a>
	 */
	@Bean("taskExecutorForLongRunningTasks")
	public ThreadPoolTaskExecutor geLongRunningTaskAsyncExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("long-running-async");
		
		//This line sets the minimum number of threads that will always be kept alive in the pool. In this case, it's set to 2, so there will always be at least 2 threads ready to work.
		executor.setCorePoolSize(2);
		
		//This line sets the maximum number of threads that the pool can have. If more tasks are added than the core pool size (2 in this case), and there are tasks waiting in a queue, new threads will be created up to this maximum limit.
		executor.setMaxPoolSize(6);
		
		// This sets the maximum number of tasks that can be waiting in a queue if all threads are busy. If the queue is full, additional tasks may be rejected or handled according to your configuration.
		executor.setQueueCapacity(5);
		
		executor.initialize();
		return executor;
	}
}
