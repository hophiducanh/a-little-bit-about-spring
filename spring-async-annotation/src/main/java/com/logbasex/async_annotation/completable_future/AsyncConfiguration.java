package com.logbasex.async_annotation.completable_future;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration 
{
	
	/**
	 * {@link <a href="https://viblo.asia/p/threadpoolexecutor-va-nguyen-tac-quan-ly-pool-size-Do754qyWKM6">...</a>}
	 * Khi có request, nó sẽ tạo trong Pool tối đa 3 thread (corePoolSize).
	 * Khi số lượng thread vượt quá 3 thread. Nó sẽ cho vào hàng đợi.
	 * Khi số lượng hàng đợi full 100 (queueCapacity). Lúc này mới bắt đầu tạo thêm Thread mới.
	 * Số thread mới được tạo tối đa là 4 (maxPoolSize).
	 * Khi Request vượt quá số lượng 4 thread. Request sẽ bị từ chối!
	 */
	@Bean(name = "asyncExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("AsynchThread-");
		executor.initialize();
		return executor;
	}
}
